package com.example.tasky.feature_agenda.data.remote.dto

data class EventDto(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val host: String,
    val isUserEventCreator: Boolean,
    val attendees: List<AttendeesDto>,
    val photos: List<PhotoDto>
)
