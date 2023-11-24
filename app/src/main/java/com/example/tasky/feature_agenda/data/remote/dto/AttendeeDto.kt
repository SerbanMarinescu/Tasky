package com.example.tasky.feature_agenda.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendeeDto(
    val email: String,
    val fullName: String,
    val userId: String
)
