package com.example.tasky.feature_agenda.presentation.event_detail_screen

import android.net.Uri
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.util.ReminderType
import com.example.tasky.feature_agenda.presentation.util.DateTimeDialogType
import com.example.tasky.feature_agenda.presentation.util.SelectableChipOptions
import java.time.LocalDate
import java.time.LocalTime

sealed class EventDetailOnClick {
    data object SaveEvent: EventDetailOnClick()
    data object ToggleEditMode: EventDetailOnClick()
    data object ToggleAddingPhotos: EventDetailOnClick()
    data object ToggleReminderMenu: EventDetailOnClick()
    data object ToggleAddingAttendeeDialog: EventDetailOnClick()
    data class SelectFilterOption(val selectedChip: SelectableChipOptions, val index: Int): EventDetailOnClick()
    data class TitleChanged(val title: String): EventDetailOnClick()
    data class DescriptionChanged(val description: String): EventDetailOnClick()
    data class DateTimePickerChanged(val dateTimeOption: DateTimeDialogType): EventDetailOnClick()
    data class FromTimeChanged(val fromTime: LocalTime): EventDetailOnClick()
    data class FromDateChanged(val fromDate: LocalDate): EventDetailOnClick()
    data class ToTimeChanged(val toTime: LocalTime): EventDetailOnClick()
    data class ToDateChanged(val toDate: LocalDate): EventDetailOnClick()
    data class ReminderTypeChanged(val reminderType: ReminderType): EventDetailOnClick()
    data class AttendeeEmailChanged(val email: String): EventDetailOnClick()
    data class AddAttendee(val email: String): EventDetailOnClick()
    data class RemoveAttendee(val attendee: Attendee): EventDetailOnClick()
    data class AddPhoto(val photoUri: Uri?): EventDetailOnClick()
    data class DeletePhoto(val photoKey: String): EventDetailOnClick()
}
