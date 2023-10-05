package com.example.tasky.feature_agenda.domain.model

import java.time.LocalDateTime

data class Event(
    val title: String,
    val description: String?,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val remindAt: LocalDateTime,
    val attendees: List<Attendee>,
    val photos: List<Photo>?
)
