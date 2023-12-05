package com.example.tasky.feature_agenda.presentation.task_detail_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.AgendaRepositories
import com.example.tasky.feature_agenda.domain.use_case.AgendaUseCases
import com.example.tasky.feature_agenda.domain.util.NotificationScheduler
import com.example.tasky.feature_agenda.domain.util.ReminderType
import com.example.tasky.util.ArgumentTypeEnum
import com.example.tasky.util.Resource
import com.example.tasky.util.Result
import com.vanpra.composematerialdialogs.MaterialDialogState
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
    private val repositories: AgendaRepositories,
    private val savedStateHandle: SavedStateHandle,
    private val notificationScheduler: NotificationScheduler
): ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state = _state.asStateFlow()

    private val resultChannel = Channel<Result<Unit>>()
    val navigationResult = resultChannel.receiveAsFlow()

    var dateDialogState by mutableStateOf(MaterialDialogState())
    var timeDialogState by mutableStateOf(MaterialDialogState())


    init {
        val taskId = savedStateHandle.get<String>(ArgumentTypeEnum.ITEM_ID.name)
        val editMode = savedStateHandle.get<String>(ArgumentTypeEnum.EDIT_MODE.name)

        taskId?.let {
            val editable = editMode != null
            getSelectedTask(it, editable)
        }
    }
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
            TaskDetailEvent.SaveTask -> {
                createOrUpdateTask()
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
                    it.copy(editMode = !it.editMode)
                }
            }

            TaskDetailEvent.DeleteTask -> {
                val taskId = savedStateHandle.get<String>(ArgumentTypeEnum.ITEM_ID.name)
                viewModelScope.launch {
                    taskId?.let {
                        useCases.task.deleteTask(it)
                        notificationScheduler.cancelNotification(it)
                        resultChannel.send(Result.Success())
                    }
                    resultChannel.send(Result.Success())
                }
            }

            TaskDetailEvent.ToggleReminderMenu -> {
                _state.update {
                    it.copy(isReminderMenuVisible = !it.isReminderMenuVisible)
                }
            }
        }
    }

    private fun createOrUpdateTask() {
        viewModelScope.launch {
            val dateTime = ZonedDateTime.of(state.value.atDate, state.value.atTime, ZoneId.systemDefault())

            val taskToBeCreated = AgendaItem.Task(
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

            val existingTaskId = state.value.taskId
            existingTaskId?.let {
                val taskToBeUpdated = taskToBeCreated.copy(taskId = it)
                useCases.task.updateTask(taskToBeUpdated)
                notificationScheduler.scheduleNotification(taskToBeUpdated)
                resultChannel.send(Result.Success())
                return@launch
            }

            useCases.task.createTask(taskToBeCreated)
            notificationScheduler.scheduleNotification(taskToBeCreated)
            resultChannel.send(Result.Success())
        }
    }

    private fun getSelectedTask(taskId: String, editable: Boolean) {
        viewModelScope.launch {
            val result = repositories.taskRepository.getTask(taskId)
            when(result) {
                is Resource.Error -> resultChannel.send(Result.Error(result.message ?: "Unknown Error"))
                is Resource.Success -> {
                    val task = result.data ?: return@launch
                    _state.update {
                        it.copy(
                            taskId = taskId,
                            editMode = editable,
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