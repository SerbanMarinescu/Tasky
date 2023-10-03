package com.example.tasky.feature_agenda.domain.model

data class Attendees(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: Long
)
