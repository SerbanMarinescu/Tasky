package com.example.tasky.feature_agenda.data.remote.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SyncAgendaRequest(
    val deletedEventIds: List<String>,
    val deletedTaskIds: List<String>,
    val deletedReminderIds: List<String>,
)
