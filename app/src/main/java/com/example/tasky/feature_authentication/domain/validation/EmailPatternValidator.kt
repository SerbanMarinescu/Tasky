package com.example.tasky.feature_authentication.domain.validation

interface EmailPatternValidator {
    fun isValid(email: String): Boolean
}