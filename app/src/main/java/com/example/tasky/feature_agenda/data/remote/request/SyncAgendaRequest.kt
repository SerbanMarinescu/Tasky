package com.example.tasky.feature_agenda.data.remote.request

data class SyncAgendaRequest(
    val deletedEventIds: List<String>,
    val deletedTaskIds: List<String>,
    val deletedReminderIds: List<String>,
)
