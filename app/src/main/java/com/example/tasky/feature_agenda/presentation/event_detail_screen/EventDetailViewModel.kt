package com.example.tasky.feature_agenda.presentation.event_detail_screen

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.repository.AgendaRepositories
import com.example.tasky.feature_agenda.domain.use_case.AgendaUseCases
import com.example.tasky.feature_agenda.domain.util.ReminderType
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.util.ArgumentTypeEnum
import com.example.tasky.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val userPreferences: UserPreferences
): ViewModel() {

    private val _state = MutableStateFlow(EventDetailState())
    val state = _state.asStateFlow()

    private val photoList = mutableStateListOf<Photo>()

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
                    it.copy(selectedChip = event.selectedChip)
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
                if(event.photoUri == null) {
                    _state.update {
                        it.copy(addingPhotos = !it.addingPhotos)
                    }
                } else {
                    photoList.add(
                        Photo(
                            key = UUID.randomUUID().toString(),
                            url = event.photoUri.toString()
                        )
                    )
                    _state.update {
                        it.copy(photoList = photoList)
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
        }
    }

    private fun saveEvent() {
        viewModelScope.launch {
        val fromDateTime = ZonedDateTime.of(state.value.fromDate, state.value.fromTime, ZoneId.systemDefault())
        val toDateTime = ZonedDateTime.of(state.value.toDate, state.value.toTime, ZoneId.systemDefault())
            val user = userPreferences.getAuthenticatedUser() ?: return@launch

            val eventToBeCreated = AgendaItem.Event(
                eventId = UUID.randomUUID().toString(),
                eventTitle = state.value.eventTitle,
                eventDescription = state.value.eventDescription,
                from = fromDateTime,
                to = toDateTime,
                photos = state.value.photoList,
                attendees = state.value.attendees,
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
                            attendees = event.attendees,
                            photoList = event.photos
                        )
                    }
                }
                else -> {

                }
            }
        }
    }
}