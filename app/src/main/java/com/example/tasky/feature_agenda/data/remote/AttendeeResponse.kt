package com.example.tasky.feature_agenda.data.remote

import com.example.tasky.feature_agenda.data.remote.dto.AttendeeDto

data class AttendeeResponse(
    val doesUserExist: Boolean,
    val attendee: AttendeeDto
)
