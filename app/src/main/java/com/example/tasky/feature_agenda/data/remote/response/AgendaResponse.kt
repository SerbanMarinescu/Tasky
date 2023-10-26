package com.example.tasky.feature_agenda.data.remote.response

import com.example.tasky.feature_agenda.data.remote.dto.EventDto
import com.example.tasky.feature_agenda.data.remote.dto.ReminderDto
import com.example.tasky.feature_agenda.data.remote.dto.TaskDto

data class AgendaResponse(
    val events: List<EventDto>,
    val tasks: List<TaskDto>,
    val reminders: List<ReminderDto>
)
