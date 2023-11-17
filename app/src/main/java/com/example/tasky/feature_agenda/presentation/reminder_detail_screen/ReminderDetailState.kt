package com.example.tasky.feature_agenda.presentation.reminder_detail_screen

import com.example.tasky.feature_agenda.domain.util.ReminderType
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

data class ReminderDetailState(
    val reminderId: String? = null,
    val editMode: Boolean = true,
    val currentDate: ZonedDateTime = ZonedDateTime.now(),
    val reminderTitle: String = "New Reminder",
    val reminderDescription: String = "Reminder Description",
    val atTime: LocalTime = LocalTime.now(),
    val atDate: LocalDate = LocalDate.now(),
    val reminderType: ReminderType = ReminderType.ONE_HOUR_BEFORE,
    val isReminderMenuVisible: Boolean = false
)
