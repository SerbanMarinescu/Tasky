package com.example.tasky.feature_authentication.presentation.register_screen

import com.example.tasky.feature_authentication.presentation.util.UiText

data class RegisterState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isFullNameValid: Boolean = false,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val fullNameError: UiText? = null,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
)