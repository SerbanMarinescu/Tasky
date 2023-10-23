package com.example.tasky.feature_authentication.domain.model

data class AuthenticatedUser(
    val fullName: String,
    val email: String,
    val token: String,
    val userId: String
)
