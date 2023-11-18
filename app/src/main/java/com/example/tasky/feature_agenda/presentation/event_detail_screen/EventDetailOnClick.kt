package com.example.tasky.feature_agenda.presentation.event_detail_screen

import android.net.Uri
import com.example.tasky.feature_agenda.presentation.util.SelectableChipOptions

sealed class EventDetailOnClick {
    data object SaveEvent: EventDetailOnClick()
    data object ToggleEditMode: EventDetailOnClick()
    data object ToggleAddingPhotos: EventDetailOnClick()
    data object ToggleReminderMenu: EventDetailOnClick()
    data object ToggleAddingAttendeeDialog: EventDetailOnClick()
    data class SelectFilterOption(val selectedChip: SelectableChipOptions): EventDetailOnClick()
    data class TitleChanged(val title: String): EventDetailOnClick()
    data class DescriptionChanged(val description: String): EventDetailOnClick()
    data class AddPhoto(val photoUri: Uri?): EventDetailOnClick()
    data class DeletePhoto(val photoKey: String): EventDetailOnClick()
}
