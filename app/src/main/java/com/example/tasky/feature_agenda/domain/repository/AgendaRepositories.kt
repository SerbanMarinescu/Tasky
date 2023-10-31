package com.example.tasky.feature_agenda.domain.repository

data class AgendaRepositories(
    val agendaRepository: AgendaRepository,
    val eventRepository: EventRepository,
    val reminderRepository: ReminderRepository,
    val taskRepository: TaskRepository
)
