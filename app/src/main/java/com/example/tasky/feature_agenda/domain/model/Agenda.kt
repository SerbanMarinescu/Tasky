package com.example.tasky.feature_agenda.domain.model

data class Agenda(
    val events: List<Event>,
    val tasks: List<Task>,
    val reminders: List<Reminder>
)
