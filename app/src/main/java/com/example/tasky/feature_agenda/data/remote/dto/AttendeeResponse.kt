package com.example.tasky.feature_agenda.data.remote.dto

import com.example.tasky.feature_agenda.domain.model.Attendee

data class AttendeeResponse(
    val doesUserExist: Boolean,
    val attendee: Attendee
)
