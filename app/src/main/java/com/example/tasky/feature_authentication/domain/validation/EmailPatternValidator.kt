package com.example.tasky.feature_authentication.domain.validation

interface EmailPatternValidator {
    fun isEmailPatternValid(email: String): Boolean
}