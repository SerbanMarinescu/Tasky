package com.example.tasky.feature_authentication.domain.validation

data class ValidationResult(
    val fullNameError: FullNameError? = null,
    val emailError: EmailError? = null,
    val passwordError: PasswordError? = null,
    val isValid: Boolean = true
)

enum class FullNameError {
    NAME_EMPTY,
    NAME_TOO_SHORT,
    NAME_TOO_LONG
}

enum class EmailError {
    EMAIL_EMPTY,
    EMAIL_INVALID
}

enum class PasswordError {
    PASSWORD_EMPTY,
    PASSWORD_INVALID,
    PASSWORD_TOO_SHORT
}