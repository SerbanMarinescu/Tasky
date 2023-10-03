package com.example.tasky.feature_agenda.domain.model

import java.time.LocalDateTime

data class Attendee(
    val email: String,
    val fullName: String,
    val userId: String
)
