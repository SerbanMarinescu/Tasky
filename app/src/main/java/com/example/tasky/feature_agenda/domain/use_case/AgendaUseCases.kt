package com.example.tasky.feature_agenda.domain.use_case

data class AgendaUseCases(
    val event: Event,
    val reminder: Reminder,
    val task: Task,
    val logout: Logout
)
