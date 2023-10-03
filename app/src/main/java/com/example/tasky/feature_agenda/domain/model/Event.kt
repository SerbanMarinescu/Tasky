package com.example.tasky.feature_agenda.domain.model

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val host: String,
    val isUserEventCreator: Boolean,
    val attendees: List<Attendees>,
    val photos: List<Photo>
)
