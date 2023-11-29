package com.example.tasky.feature_agenda.presentation.reminder_detail_screen

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
class ReminderDetailViewModel @Inject constructor(
    private val useCases: AgendaUseCases,
    private val repositories: AgendaRepositories,
    private val savedStateHandle: SavedStateHandle,
    private val notificationScheduler: NotificationScheduler
): ViewModel() {

    private val _state = MutableStateFlow(ReminderDetailState())
    val state = _state.asStateFlow()

    private val resultChannel = Channel<Result<Unit>>()
    val navigationResult = resultChannel.receiveAsFlow()

    var dateDialogState by mutableStateOf(MaterialDialogState())
    var timeDialogState by mutableStateOf(MaterialDialogState())


    init {
        val reminderId = savedStateHandle.get<String>(ArgumentTypeEnum.ITEM_ID.name)
        val editMode = savedStateHandle.get<String>(ArgumentTypeEnum.EDIT_MODE.name)

        reminderId?.let { id ->
            val editable = editMode != null
            getSelectedReminder(id, editable)
        }
    }
    fun onEvent(event: ReminderDetailEvent) {
        when(event) {
            is ReminderDetailEvent.AtDateChanged -> {
                _state.update {
                    it.copy(atDate = event.date)
                }
            }
            is ReminderDetailEvent.AtTimeChanged -> {
                _state.update {
                    it.copy(atTime = event.time)
                }
            }
            ReminderDetailEvent.SaveReminder -> {
                createOrUpdateReminder()
            }
            is ReminderDetailEvent.DescriptionChanged -> {
                _state.update {
                    it.copy(reminderDescription = event.description)
                }
            }
            is ReminderDetailEvent.ReminderTypeChanged -> {
                _state.update {
                    it.copy(reminderType = event.reminderType)
                }
            }
            is ReminderDetailEvent.TitleChanged -> {
                _state.update {
                    it.copy(reminderTitle = event.title)
                }
            }
            ReminderDetailEvent.ToggleEditMode -> {
                _state.update {
                    it.copy(editMode = !it.editMode)
                }
            }

            ReminderDetailEvent.DeleteReminder -> {
                val reminderId = savedStateHandle.get<String>(ArgumentTypeEnum.ITEM_ID.name)
                viewModelScope.launch {
                    reminderId?.let {
                        useCases.reminder.deleteReminder(it)
                        notificationScheduler.cancelNotification(it)
                        resultChannel.send(Result.Success())
                    }
                    resultChannel.send(Result.Success())
                }
            }

            ReminderDetailEvent.ToggleReminderMenu -> {
                _state.update {
                    it.copy(isReminderMenuVisible = !it.isReminderMenuVisible)
                }
            }
        }
    }

    private fun createOrUpdateReminder() {
        viewModelScope.launch {
            val dateTime = ZonedDateTime.of(state.value.atDate, state.value.atTime, ZoneId.systemDefault())

            val reminderToBeCreated = AgendaItem.Reminder(
                reminderId = UUID.randomUUID().toString() ,
                reminderTitle = state.value.reminderTitle,
                reminderDescription = state.value.reminderDescription,
                time = dateTime,
                remindAtTime = when(state.value.reminderType) {
                    ReminderType.TEN_MINUTES_BEFORE -> dateTime.minusMinutes(10)
                    ReminderType.THIRTY_MINUTES_BEFORE -> dateTime.minusMinutes(30)
                    ReminderType.ONE_HOUR_BEFORE -> dateTime.minusHours(1)
                    ReminderType.SIX_HOURS_BEFORE -> dateTime.minusHours(6)
                    ReminderType.ONE_DAY_BEFORE -> dateTime.minusDays(1)
                },
                typeOfReminder = state.value.reminderType
            )

            val existingReminderId = state.value.reminderId
            existingReminderId?.let {
                val reminderToBeUpdated = reminderToBeCreated.copy(reminderId = it)
                useCases.reminder.updateReminder(reminderToBeUpdated)
                notificationScheduler.scheduleNotification(reminderToBeUpdated)
                resultChannel.send(Result.Success())
                return@launch
            }

            useCases.reminder.createReminder(reminderToBeCreated)
            notificationScheduler.scheduleNotification(reminderToBeCreated)
            resultChannel.send(Result.Success())
        }
    }

    private fun getSelectedReminder(reminderId: String, editable: Boolean) {
        viewModelScope.launch {
            val result = repositories.reminderRepository.getReminder(reminderId)
            when(result) {
                is Resource.Error -> resultChannel.send(Result.Error(result.message ?: "Unknown Error"))
                is Resource.Success -> {
                    val reminder = result.data ?: return@launch
                    _state.update {
                        it.copy(
                            reminderId = reminderId,
                            editMode = editable,
                            currentDate = reminder.time,
                            reminderTitle = reminder.reminderTitle,
                            reminderDescription = reminder.reminderDescription ?: "Task Description",
                            atTime = reminder.time.toLocalTime(),
                            atDate = reminder.time.toLocalDate(),
                            reminderType = reminder.typeOfReminder
                        )
                    }
                }
                else -> resultChannel.send(Result.Error(result.message ?: "Unknown Error"))
            }
        }
    }
}