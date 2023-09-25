package com.example.tasky.feature_authentication.domain.util

import com.example.tasky.feature_authentication.domain.validation.EmailError
import com.example.tasky.feature_authentication.domain.validation.FullNameError
import com.example.tasky.feature_authentication.domain.validation.PasswordError

sealed class AuthUseCaseResult {
    data class ErrorFullName(val fullNameError: FullNameError): AuthUseCaseResult()
    data class ErrorEmail(val emailError: EmailError): AuthUseCaseResult()
    data class ErrorPassword(val passwordError: PasswordError): AuthUseCaseResult()
    object Success: AuthUseCaseResult()
    data class GenericError(val message: String): AuthUseCaseResult()
}
