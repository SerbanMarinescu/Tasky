package com.example.tasky.feature_agenda.domain.model

import java.time.ZonedDateTime

data class Attendee(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: ZonedDateTime
)
