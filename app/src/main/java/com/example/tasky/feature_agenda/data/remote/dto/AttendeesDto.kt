package com.example.tasky.feature_agenda.data.remote.dto

data class AttendeesDto(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: Long
)
