package com.example.tasky.feature_authentication.domain.util

import com.example.tasky.feature_authentication.domain.validation.EmailError
import com.example.tasky.feature_authentication.domain.validation.FullNameError
import com.example.tasky.feature_authentication.domain.validation.PasswordError

sealed class UseCaseResult {
    data class ErrorFullName(val fullNameError: FullNameError): UseCaseResult()
    data class ErrorEmail(val emailError: EmailError): UseCaseResult()
    data class ErrorPassword(val passwordError: PasswordError): UseCaseResult()
    object Authorized: UseCaseResult()
    data class GenericError(val message: String): UseCaseResult()
}
