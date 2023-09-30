package com.example.tasky.feature_authentication.presentation.login_screen

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
)
