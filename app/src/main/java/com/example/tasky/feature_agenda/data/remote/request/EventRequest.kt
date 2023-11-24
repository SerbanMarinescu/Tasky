package com.example.tasky.feature_agenda.data.remote.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventRequest(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val attendeeIds: List<String>
)
