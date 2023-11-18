package com.example.tasky.feature_agenda.presentation.event_detail_screen

import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.util.ReminderType
import com.example.tasky.feature_agenda.presentation.util.SelectableChipOptions
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

data class EventDetailState(
    val eventId: String? = null,
    val editMode: Boolean = true,
    val currentDate: ZonedDateTime = ZonedDateTime.now(),
    val eventTitle: String = "New Event",
    val eventDescription: String = "Event Description",
    val fromTime: LocalTime = LocalTime.now(),
    val fromDate: LocalDate = LocalDate.now(),
    val toDate: LocalDate = LocalDate.now(),
    val toTime: LocalTime = LocalTime.now().plusMinutes(30),
    val reminderType: ReminderType = ReminderType.ONE_HOUR_BEFORE,
    val isReminderMenuVisible: Boolean = false,
    val selectedChipIndex: Int = 0,
    val selectedChip: SelectableChipOptions = SelectableChipOptions.ALL,
    val addingPhotos: Boolean = false,
    val addingAttendees: Boolean = false,
    val photoList: List<Photo> = emptyList(),
    val attendees: List<Attendee> = emptyList()
)
