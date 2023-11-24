package com.example.tasky.feature_authentication.data.remote.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationRequest(
    val fullName: String,
    val email: String,
    val password: String
)