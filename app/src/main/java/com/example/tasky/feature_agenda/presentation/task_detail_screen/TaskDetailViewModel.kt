package com.example.tasky.feature_agenda.presentation.task_detail_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.AgendaRepositories
import com.example.tasky.feature_agenda.domain.use_case.AgendaUseCases
import com.example.tasky.feature_agenda.domain.util.ReminderType
import com.example.tasky.util.Resource
import com.example.tasky.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val useCases: AgendaUseCases,
    private val repositories: AgendaRepositories
): ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state = _state.asStateFlow()

    private val resultChannel = Channel<Result<Unit>>()
    val logoutResult = resultChannel.receiveAsFlow()

    fun onEvent(event: TaskDetailEvent) {
        when(event) {
            is TaskDetailEvent.AtDateChanged -> {
                _state.update {
                    it.copy(atDate = event.date)
                }
            }
            is TaskDetailEvent.AtTimeChanged -> {
                _state.update {
                    it.copy(atTime = event.time)
                }
            }
            TaskDetailEvent.CreateTask -> {
                createTask()
            }
            is TaskDetailEvent.DescriptionChanged -> {
                _state.update {
                    it.copy(taskDescription = event.description)
                }
            }
            is TaskDetailEvent.ReminderTypeChanged -> {
                _state.update {
                    it.copy(reminderType = event.reminderType)
                }
            }
            is TaskDetailEvent.TitleChanged -> {
                _state.update {
                    it.copy(taskTitle = event.title)
                }
            }
            TaskDetailEvent.ToggleEditMode -> {
                _state.update {
                    it.copy(editMode = !state.value.editMode)
                }
            }

            is TaskDetailEvent.GetOpenedTask -> {
                getSelectedTask(event.taskId)
            }

            is TaskDetailEvent.DeleteTask -> {
                val taskId = event.taskId
                taskId?.let {
                    viewModelScope.launch {
                        val result = repositories.taskRepository.getTask(it)
                        when(result) {
                            is Resource.Error -> resultChannel.send(Result.Error(result.message ?: "Unknown Error"))
                            is Resource.Success -> {
                                val task = result.data ?: return@launch
                                repositories.taskRepository.deleteTask(task)
                            }
                            else -> resultChannel.send(Result.Error(result.message ?: "Unknown Error"))
                        }
                    }
                }
            }
        }
    }

    private fun createTask() {
        viewModelScope.launch {
            val dateTime = ZonedDateTime.of(state.value.atDate, state.value.atTime, ZoneId.systemDefault())

            val task = AgendaItem.Task(
                taskId = UUID.randomUUID().toString() ,
                taskTitle = state.value.taskTitle,
                taskDescription = state.value.taskDescription,
                time = dateTime,
                isDone = false,
                remindAtTime = when(state.value.reminderType) {
                    ReminderType.TEN_MINUTES_BEFORE -> dateTime.minusMinutes(10)
                    ReminderType.THIRTY_MINUTES_BEFORE -> dateTime.minusMinutes(30)
                    ReminderType.ONE_HOUR_BEFORE -> dateTime.minusHours(1)
                    ReminderType.SIX_HOURS_BEFORE -> dateTime.minusHours(6)
                    ReminderType.ONE_DAY_BEFORE -> dateTime.minusDays(1)
                },
                taskReminderType = state.value.reminderType
            )

            useCases.task.createTask(task)
        }
    }

    private fun getSelectedTask(taskId: String) {
        viewModelScope.launch {
            val result = repositories.taskRepository.getTask(taskId)
            when(result) {
                is Resource.Error -> resultChannel.send(Result.Error(result.message ?: "Unknown Error"))
                is Resource.Success -> {
                    val task = result.data ?: return@launch
                    _state.update {
                        it.copy(
                            editMode = false,
                            currentDate = task.time,
                            taskTitle = task.taskTitle,
                            taskDescription = task.taskDescription ?: "Task Description",
                            atTime = task.time.toLocalTime(),
                            atDate = task.time.toLocalDate(),
                            reminderType = task.taskReminderType
                        )
                    }
                }
                else -> resultChannel.send(Result.Error(result.message ?: "Unknown Error"))
            }
        }
    }
}