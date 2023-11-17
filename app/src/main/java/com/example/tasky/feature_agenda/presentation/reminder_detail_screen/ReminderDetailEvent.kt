package com.example.tasky.feature_agenda.presentation.reminder_detail_screen

import com.example.tasky.feature_agenda.domain.util.ReminderType
import java.time.LocalDate
import java.time.LocalTime

sealed class ReminderDetailEvent {
    data object ToggleEditMode: ReminderDetailEvent()
    data class TitleChanged(val title: String): ReminderDetailEvent()
    data class DescriptionChanged(val description: String): ReminderDetailEvent()
    data class AtTimeChanged(val time: LocalTime): ReminderDetailEvent()
    data class AtDateChanged(val date: LocalDate): ReminderDetailEvent()
    data class ReminderTypeChanged(val reminderType: ReminderType): ReminderDetailEvent()
    data object ToggleReminderMenu: ReminderDetailEvent()
    data object DeleteReminder: ReminderDetailEvent()
    data object SaveReminder: ReminderDetailEvent()
}
