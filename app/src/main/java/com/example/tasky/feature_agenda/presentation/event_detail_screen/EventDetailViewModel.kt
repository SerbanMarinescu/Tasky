package com.example.tasky.feature_agenda.presentation.event_detail_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.R
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.EventPhoto
import com.example.tasky.feature_agenda.domain.repository.AgendaRepositories
import com.example.tasky.feature_agenda.domain.use_case.AgendaUseCases
import com.example.tasky.feature_agenda.domain.util.PhotoValidator
import com.example.tasky.feature_agenda.domain.util.ReminderType
import com.example.tasky.feature_agenda.presentation.util.validateDateRange
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
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
    private val userDataValidator: UserDataValidator,
    private val photoValidator: PhotoValidator
): ViewModel() {

    private val _state = MutableStateFlow(EventDetailState())
    val state = _state.asStateFlow()

    private val resultChannel = Channel<Result<Unit>>()
    val validationResult = resultChannel.receiveAsFlow()

    var photoList = mutableStateListOf<EventPhoto>()
        private set
    var attendeeList = mutableStateListOf<Attendee>()
        private set

    private val deletedPhotos = mutableListOf<EventPhoto>()

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
                if(state.value.eventId == null) {
                    createEvent()
                } else {
                    updateEvent()
                }
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
                if(event.photoUri == null && photoList.isEmpty()) {
                    _state.update {
                        it.copy(addingPhotos = !it.addingPhotos)
                    }
                } else {
                    event.photoUri?.let { uri ->
                        photoList.add(
                            EventPhoto.Local(
                                key = UUID.randomUUID().toString(),
                                uri = uri.toString(),
                                byteArray = byteArrayOf()
                            )
                        )
                    }
                }
            }

            is EventDetailOnClick.DeletePhoto -> {
                val photoToRemove = photoList.find { photo ->
                when(photo) {
                    is EventPhoto.Local -> photo.key == event.photoKey
                    is EventPhoto.Remote -> photo.key == event.photoKey
                }
                }

                photoToRemove?.let { photo ->
                    deletedPhotos.add(photo)
                    photoList.remove(photo)

                    _state.update {
                        it.copy(
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
                    it.copy(
                        fromDate = event.fromDate,
                        toDate = if(!validateDateRange(event.fromDate, it.toDate))
                            event.fromDate.plusDays(1) else it.toDate
                    )
                }
            }
            is EventDetailOnClick.FromTimeChanged -> {
                _state.update {
                    it.copy(
                        fromTime = event.fromTime,
                        toTime = if(!validateDateRange(event.fromTime, it.toTime))
                        event.fromTime.plusMinutes(30) else it.toTime
                    )
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
                    val result = useCases.event.validateAttendee(event.email)

                    when(result) {
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
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
                        is Resource.Success -> {
                            val response = result.data ?: return@launch
                            val attendee = Attendee(
                                email = response.email,
                                fullName = response.fullName,
                                userId = response.userId,
                                eventId = state.value.eventId ?: "",
                                isGoing = true,
                                remindAt = ZonedDateTime.of(state.value.fromDate, state.value.fromTime, ZoneId.systemDefault())
                            )

                            if(attendeeList.contains(attendee)) {
                                resultChannel.send(Result.Error("Attendee Already Added!"))
                                return@launch
                            }

                            attendeeList.add(attendee)
                            _state.update {
                                it.copy(
                                    addingAttendees = !it.addingAttendees,
                                    attendeeEmail = ""
                                )
                            }
                        }
                        else -> Unit
                    }
                }
            }

            is EventDetailOnClick.RemoveAttendee -> {
                attendeeList.remove(event.attendee)
            }
        }
    }

    private suspend fun startLoading() {
        (0..100).forEach { progress ->
            _state.update {
                it.copy(loadingProgress = progress.toFloat()/100)
            }
            delay(10L)
        }
    }

    private fun createEvent() {
        viewModelScope.launch {
            val creatingEventJob = async {
                _state.update {
                    it.copy(isLoading = true)
                }
                val fromDateTime = ZonedDateTime.of(state.value.fromDate, state.value.fromTime, ZoneId.systemDefault())
                val toDateTime = ZonedDateTime.of(state.value.toDate, state.value.toTime, ZoneId.systemDefault())

                val isDateValid = validateDateRange(fromDateTime, toDateTime)
                if (!isDateValid) {
                    resultChannel.send(Result.Error("\"From\" DateTime must not be after \"To\" DateTime"))
                    return@async
                }

                val user = userPreferences.getAuthenticatedUser() ?: return@async
                val eventId = UUID.randomUUID().toString()

                val eventCreator = Attendee(
                    email = user.email,
                    fullName = user.fullName,
                    userId = user.userId,
                    eventId = eventId,
                    isGoing = true,
                    remindAt = when (state.value.reminderType) {
                        ReminderType.TEN_MINUTES_BEFORE -> fromDateTime.minusMinutes(10)
                        ReminderType.THIRTY_MINUTES_BEFORE -> fromDateTime.minusMinutes(30)
                        ReminderType.ONE_HOUR_BEFORE -> fromDateTime.minusHours(1)
                        ReminderType.SIX_HOURS_BEFORE -> fromDateTime.minusHours(6)
                        ReminderType.ONE_DAY_BEFORE -> fromDateTime.minusDays(1)
                    }
                )

                if(!attendeeList.contains(eventCreator)) {
                    attendeeList.add(0, eventCreator)
                }

                val localPhotos = photoList.filterIsInstance<EventPhoto.Local>().toMutableList()
                val validPhotos = useCases.event.validatePhotos(localPhotos)
                val skippedPhotosCount = localPhotos.size - validPhotos.size

                if (skippedPhotosCount > 0) {
                    resultChannel.send(Result.Error("$skippedPhotosCount Photos were skipped because they were too large!"))
                }

                val eventToBeCreated = AgendaItem.Event(
                    eventId = eventId,
                    eventTitle = state.value.eventTitle,
                    eventDescription = state.value.eventDescription,
                    from = fromDateTime,
                    to = toDateTime,
                    photos = validPhotos,
                    attendees = attendeeList.map { it.copy(eventId = eventId) },
                    isUserEventCreator = true,
                    host = user.userId,
                    remindAtTime = when (state.value.reminderType) {
                        ReminderType.TEN_MINUTES_BEFORE -> fromDateTime.minusMinutes(10)
                        ReminderType.THIRTY_MINUTES_BEFORE -> fromDateTime.minusMinutes(30)
                        ReminderType.ONE_HOUR_BEFORE -> fromDateTime.minusHours(1)
                        ReminderType.SIX_HOURS_BEFORE -> fromDateTime.minusHours(6)
                        ReminderType.ONE_DAY_BEFORE -> fromDateTime.minusDays(1)
                    },
                    eventReminderType = state.value.reminderType
                )

                useCases.event.createEvent(eventToBeCreated)
            }

            val loadingJob = async {
                startLoading()
            }

            creatingEventJob.await()
            loadingJob.cancel()
            _state.update { it.copy(loadingProgress = 1f) }
            delay(500L)
            _state.update { it.copy(isLoading = false) }
            delay(50L)
            resultChannel.send(Result.Success())
        }
    }

    private fun updateEvent() {
        viewModelScope.launch {
            val fromDateTime = ZonedDateTime.of(state.value.fromDate, state.value.fromTime, ZoneId.systemDefault())
            val toDateTime = ZonedDateTime.of(state.value.toDate, state.value.toTime, ZoneId.systemDefault())

            val isDateValid = validateDateRange(fromDateTime, toDateTime)
            if(!isDateValid) {
                resultChannel.send(Result.Error("\"From\" DateTime must not be after \"To\" DateTime"))
                return@launch
            }

            val localPhotos = photoList.filterIsInstance<EventPhoto.Local>().toMutableList()
            val validPhotos = useCases.event.validatePhotos(localPhotos)
            val skippedPhotosCount = localPhotos.size - validPhotos.size

            if (skippedPhotosCount > 0) {
                resultChannel.send(Result.Error("$skippedPhotosCount Photos were skipped because they were too large!"))
            }

            state.value.eventId?.let {
                val eventToBeUpdated = AgendaItem.Event(
                    eventId = it,
                    eventTitle = state.value.eventTitle,
                    eventDescription = state.value.eventDescription,
                    from = fromDateTime,
                    to = toDateTime,
                    photos = validPhotos,
                    attendees = attendeeList.toList(),
                    isUserEventCreator = true,
                    host = state.value.eventCreatorId,
                    remindAtTime = when(state.value.reminderType) {
                        ReminderType.TEN_MINUTES_BEFORE -> fromDateTime.minusMinutes(10)
                        ReminderType.THIRTY_MINUTES_BEFORE -> fromDateTime.minusMinutes(30)
                        ReminderType.ONE_HOUR_BEFORE -> fromDateTime.minusHours(1)
                        ReminderType.SIX_HOURS_BEFORE -> fromDateTime.minusHours(6)
                        ReminderType.ONE_DAY_BEFORE -> fromDateTime.minusDays(1)
                    },
                    eventReminderType = state.value.reminderType
                )
                useCases.event.updateEvent(eventToBeUpdated, deletedPhotos)
                resultChannel.send(Result.Success())
            }
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

                    event.photos.forEach {
                        photoList.add(it)
                    }
                    event.attendees.forEach {
                        attendeeList.add(it)
                    }

                    attendeeList = attendeeList.sortedBy { it.userId != event.host }.toMutableStateList()

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
                            addingPhotos = photoList.isNotEmpty(),
                            eventCreatorId = event.host ?: ""
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