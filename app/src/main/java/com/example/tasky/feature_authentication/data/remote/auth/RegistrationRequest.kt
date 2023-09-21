package com.example.tasky.feature_authentication.data.remote.auth

data class RegistrationRequest(
    val fullName: String,
    val email: String,
    val password: String
)