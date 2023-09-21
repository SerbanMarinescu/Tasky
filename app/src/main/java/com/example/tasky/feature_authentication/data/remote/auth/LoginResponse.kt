package com.example.tasky.feature_authentication.data.remote.auth

data class LoginResponse(
    val token: String,
    val userId: String,
    val fullName: String
)
