package com.example.tasky.feature_authentication.presentation.login_screen

sealed class LoginEvent {
    data class EmailChanged(val email: String): LoginEvent()
    data class PasswordChanged(val password: String): LoginEvent()
    data object ChangePasswordVisibility: LoginEvent()
    data object SignIn: LoginEvent()
}
