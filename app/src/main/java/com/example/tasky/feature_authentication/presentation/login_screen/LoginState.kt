package com.example.tasky.feature_authentication.presentation.login_screen

import com.example.tasky.feature_authentication.presentation.util.UiText

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val emailError: UiText? = null,
    val passwordError: UiText? = null
)
