package com.example.tasky.feature_agenda.presentation.task_detail_screen

import com.example.tasky.feature_agenda.domain.util.ReminderType
import java.time.LocalDate
import java.time.LocalTime

sealed class TaskDetailEvent {
    data object ToggleEditMode: TaskDetailEvent()
    data class TitleChanged(val title: String): TaskDetailEvent()
    data class DescriptionChanged(val description: String): TaskDetailEvent()
    data class AtTimeChanged(val time: LocalTime): TaskDetailEvent()
    data class AtDateChanged(val date: LocalDate): TaskDetailEvent()
    data class ReminderTypeChanged(val reminderType: ReminderType): TaskDetailEvent()
    data class GetOpenedTask(val taskId: String): TaskDetailEvent()
    data class DeleteTask(val taskId: String?): TaskDetailEvent()
    data object CreateTask: TaskDetailEvent()
}
