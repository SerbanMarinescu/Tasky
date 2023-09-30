package com.example.tasky.feature_authentication.presentation.register_screen

sealed class RegisterEvent {
    data class FullNameChanged(val fullName: String): RegisterEvent()
    data class EmailChanged(val email: String): RegisterEvent()
    data class PasswordChanged(val password: String): RegisterEvent()
    data object ChangePasswordVisibility: RegisterEvent()
    data object SignUp: RegisterEvent()
}
