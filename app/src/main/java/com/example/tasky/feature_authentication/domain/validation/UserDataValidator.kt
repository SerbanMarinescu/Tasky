package com.example.tasky.feature_authentication.domain.validation

interface UserDataValidator {
    fun validateFullName(fullName: String): ValidationResult
    fun validateEmail(email: String): ValidationResult
    fun validatePassword(password: String): ValidationResult
}