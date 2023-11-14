package com.example.tasky.feature_agenda.presentation.task_detail_screen

import com.example.tasky.feature_agenda.domain.util.ReminderType
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

data class TaskDetailState(
    val editMode: Boolean = true,
    val currentDate: ZonedDateTime = ZonedDateTime.now(),
    val taskTitle: String = "New Task",
    val taskDescription: String = "Task Description",
    val atTime: LocalTime = LocalTime.now(),
    val atDate: LocalDate = LocalDate.now(),
    val reminderType: ReminderType = ReminderType.ONE_HOUR_BEFORE
)
