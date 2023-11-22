package com.example.tasky.feature_agenda.presentation.event_detail_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.R
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.repository.AgendaRepositories
import com.example.tasky.feature_agenda.domain.use_case.AgendaUseCases
import com.example.tasky.feature_agenda.domain.util.ReminderType
import com.example.tasky.feature_agenda.presentation.util.validateDates
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.feature_authentication.domain.validation.EmailError
import com.example.tasky.feature_authentication.domain.validation.EmailError.EMAIL_EMPTY
import com.example.tasky.feature_authentication.domain.validation.EmailError.EMAIL_INVALID
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator
import com.example.tasky.feature_authentication.presentation.util.UiText
import com.example.tasky.util.ArgumentTypeEnum
import com.example.tasky.util.ErrorType.HTTP
import com.example.tasky.util.ErrorType.IO
import com.example.tasky.util.ErrorType.OTHER
import com.example.tasky.util.ErrorType.VALIDATION_ERROR
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
class EventDetailViewModel @Inject constructor(
    private val useCases: AgendaUseCases,
    private val repositories: AgendaRepositories,
    private val savedStateHandle: SavedStateHandle,
    private val userPreferences: UserPreferences,
    private val userDataValidator: UserDataValidator
): ViewModel() {

    private val _state = MutableStateFlow(EventDetailState())
    val state = _state.asStateFlow()

    private val resultChannel = Channel<Result<Unit>>()
    val validationResult = resultChannel.receiveAsFlow()

    private val photoList = mutableStateListOf<Photo>()
    var attendeeList = mutableStateListOf<Attendee>()
        private set

    var dateDialogState by mutableStateOf(MaterialDialogState())
    var timeDialogState by mutableStateOf(MaterialDialogState())

    init {
        val eventId = savedStateHandle.get<String>(ArgumentTypeEnum.ITEM_ID.name)
        val editMode = savedStateHandle.get<String>(ArgumentTypeEnum.EDIT_MODE.name)

        eventId?.let {
            val editable = editMode != null
            getSelectedEvent(it, editable)
        }
    }

    fun onEvent(event: EventDetailOnClick) {
        when(event) {
            EventDetailOnClick.SaveEvent -> {
                //saveEvent()
            }
            is EventDetailOnClick.SelectFilterOption -> {
                _state.update {
                    it.copy(
                        selectedChip = event.selectedChip,
                        selectedChipIndex = event.index
                    )
                }
            }
            EventDetailOnClick.ToggleAddingAttendeeDialog -> {
                _state.update {
                    it.copy(addingAttendees = !it.addingAttendees)
                }
            }
            EventDetailOnClick.ToggleAddingPhotos -> {
                _state.update {
                    it.copy(addingPhotos = !it.addingPhotos)
                }
            }
            EventDetailOnClick.ToggleEditMode -> {
                _state.update {
                    it.copy(editMode = !it.editMode)
                }
            }
            EventDetailOnClick.ToggleReminderMenu -> {
                _state.update {
                    it.copy(isReminderMenuVisible = !it.isReminderMenuVisible)
                }
            }

            is EventDetailOnClick.DescriptionChanged -> {
                _state.update {
                    it.copy(eventDescription = event.description)
                }
            }
            is EventDetailOnClick.TitleChanged -> {
                _state.update {
                    it.copy(eventTitle = event.title)
                }
            }

            is EventDetailOnClick.AddPhoto -> {
                if(event.photoUri == null && state.value.photoList.isEmpty()) {
                    _state.update {
                        it.copy(addingPhotos = !it.addingPhotos)
                    }
                } else {
                    event.photoUri?.let { uri ->
                        photoList.add(
                            Photo(
                                key = UUID.randomUUID().toString(),
                                url = uri.toString()
                            )
                        )
                        _state.update {
                            it.copy(photoList = photoList)
                        }
                    }
                }
            }

            is EventDetailOnClick.DeletePhoto -> {
                val photoToRemove = photoList.find { it.key == event.photoKey }

                photoToRemove?.let { photo ->
                    photoList.remove(photo)
                    _state.update {
                        it.copy(
                            photoList = photoList,
                            addingPhotos = photoList.isNotEmpty()
                        )
                    }
                }
            }

            is EventDetailOnClick.DateTimePickerChanged -> {
                _state.update {
                    it.copy(dateTimePicker = event.dateTimeOption)
                }
            }

            is EventDetailOnClick.FromDateChanged -> {
                _state.update {
                    it.copy(fromDate = event.fromDate)
                }
            }
            is EventDetailOnClick.FromTimeChanged -> {
                _state.update {
                    it.copy(fromTime = event.fromTime)
                }
            }
            is EventDetailOnClick.ToDateChanged -> {
                _state.update {
                    it.copy(toDate = event.toDate)
                }
            }
            is EventDetailOnClick.ToTimeChanged -> {
                _state.update {
                    it.copy(toTime = event.toTime)
                }
            }

            is EventDetailOnClick.ReminderTypeChanged -> {
                _state.update {
                    it.copy(reminderType = event.reminderType)
                }
            }

            is EventDetailOnClick.AttendeeEmailChanged -> {
                _state.update {
                    it.copy(attendeeEmail = event.email)
                }
                validateEmail(state.value.attendeeEmail)
            }

            is EventDetailOnClick.AddAttendee -> {
                viewModelScope.launch {
                    useCases.event.validateAttendee(event.email).collect { result ->
                        when(result) {
                            is Resource.Error -> {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        emailError = when(result.errorType) {
                                            HTTP,IO,OTHER,null -> {
                                                resultChannel.send(Result.Error(result.message ?: "Unknown Error"))
                                                null
                                            }
                                            VALIDATION_ERROR -> {
                                                val errorType = result.message?.let { error -> enumValueOf<EmailError>(error) }
                                                when(errorType) {
                                                    EMAIL_EMPTY -> UiText.StringResource(R.string.EMAIL_EMPTY)
                                                    EMAIL_INVALID -> UiText.StringResource(R.string.EMAIL_INVALID)
                                                    null -> null
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            is Resource.Loading -> {
                                _state.update {
                                    it.copy(isLoading = !it.isLoading)
                                }
                            }
                            is Resource.Success -> {
                                val response = result.data ?: return@collect
                                val attendee = Attendee(
                                    email = response.email,
                                    fullName = response.fullName,
                                    userId = response.userId,
                                    eventId = state.value.eventId ?: "",
                                    isGoing = true,
                                    remindAt = ZonedDateTime.of(state.value.fromDate, state.value.fromTime, ZoneId.systemDefault())
                                )
                                attendeeList.add(attendee)
                                _state.update {
                                    it.copy(
                                        isLoading = !it.isLoading,
                                        addingAttendees = !it.addingAttendees,
                                        attendeeEmail = ""
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is EventDetailOnClick.RemoveAttendee -> {
                attendeeList.remove(event.attendee)
            }
        }
    }

    private fun saveEvent() {
        viewModelScope.launch {
        val fromDateTime = ZonedDateTime.of(state.value.fromDate, state.value.fromTime, ZoneId.systemDefault())
        val toDateTime = ZonedDateTime.of(state.value.toDate, state.value.toTime, ZoneId.systemDefault())

            val isDateValid = validateDates(fromDateTime, toDateTime)
            if(!isDateValid) {
                resultChannel.send(Result.Error("\"From\" DateTime must not be after \"To\" DateTime"))
            }

            val user = userPreferences.getAuthenticatedUser() ?: return@launch
            val eventId = UUID.randomUUID().toString()
            val eventCreator = Attendee(
                email = user.email,
                fullName = user.fullName,
                userId = user.userId,
                eventId = eventId,
                isGoing = true,
                remindAt = when(state.value.reminderType) {
                    ReminderType.TEN_MINUTES_BEFORE -> fromDateTime.minusMinutes(10)
                    ReminderType.THIRTY_MINUTES_BEFORE -> fromDateTime.minusMinutes(30)
                    ReminderType.ONE_HOUR_BEFORE -> fromDateTime.minusHours(1)
                    ReminderType.SIX_HOURS_BEFORE -> fromDateTime.minusHours(6)
                    ReminderType.ONE_DAY_BEFORE -> fromDateTime.minusDays(1)
                }
            )

            attendeeList.add(0,eventCreator)

            val eventToBeCreated = AgendaItem.Event(
                eventId = eventId,
                eventTitle = state.value.eventTitle,
                eventDescription = state.value.eventDescription,
                from = fromDateTime,
                to = toDateTime,
                photos = state.value.photoList,
                attendees = attendeeList.map { it.copy(eventId = eventId) },
                isUserEventCreator = true,
                host = user.userId,
                remindAtTime = when(state.value.reminderType) {
                    ReminderType.TEN_MINUTES_BEFORE -> fromDateTime.minusMinutes(10)
                    ReminderType.THIRTY_MINUTES_BEFORE -> fromDateTime.minusMinutes(30)
                    ReminderType.ONE_HOUR_BEFORE -> fromDateTime.minusHours(1)
                    ReminderType.SIX_HOURS_BEFORE -> fromDateTime.minusHours(6)
                    ReminderType.ONE_DAY_BEFORE -> fromDateTime.minusDays(1)
                },
                eventReminderType = state.value.reminderType
            )

            val existingEventId = state.value.eventId
            existingEventId?.let {
                val eventToBeUpdated = eventToBeCreated.copy(eventId = it)
                useCases.event.updateEvent(eventToBeUpdated)
                return@launch
            }

            useCases.event.createEvent(eventToBeCreated)
        }
    }

    private fun getSelectedEvent(eventId: String, editable: Boolean) {
        viewModelScope.launch {
            val result = repositories.eventRepository.getEvent(eventId)

            when(result) {
                is Resource.Error -> {

                }
                is Resource.Success -> {
                    val event = result.data ?: return@launch
                    _state.update {
                        it.copy(
                            eventId = eventId,
                            editMode = editable,
                            eventTitle = event.eventTitle,
                            eventDescription = event.eventDescription ?: "",
                            fromDate = event.from.toLocalDate(),
                            fromTime = event.from.toLocalTime(),
                            toDate = event.to.toLocalDate(),
                            toTime = event.to.toLocalTime(),
                            currentDate = event.from,
                            reminderType = event.eventReminderType,
                            attendeeList = event.attendees,
                            photoList = event.photos
                        )
                    }
                }
                else -> {

                }
            }
        }
    }

    private fun validateEmail(email: String) {
        val result = userDataValidator.validateEmail(email)

        if(result.isValid) {
            _state.update {
                it.copy(
                    isEmailValid = true,
                    emailError = null
                )
            }
        } else {
            _state.update {
                it.copy(
                    isEmailValid = false
                )
            }
        }
    }
}