package com.example.tasky.feature_agenda.data.remote.dto

import com.example.tasky.feature_agenda.domain.model.Event
import com.example.tasky.feature_agenda.domain.model.Reminder
import com.example.tasky.feature_agenda.domain.model.Task

data class AgendaResponse(
    val events: List<Event>,
    val tasks: List<Task>,
    val reminders: List<Reminder>
)
