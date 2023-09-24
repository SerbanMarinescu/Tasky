package com.example.tasky.feature_authentication.domain.use_case

data class AuthUseCases(
    val register: Register,
    val login: Login,
    val authenticate: Authenticate
)
