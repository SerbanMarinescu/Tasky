package com.example.tasky.feature_agenda.data.remote.response

import com.example.tasky.feature_agenda.data.remote.dto.AttendeeDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendeeResponse(
    val doesUserExist: Boolean,
    val attendee: AttendeeDto?
)
