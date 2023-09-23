package com.example.tasky.feature_authentication.domain.validation

interface EmailMatcher {
    fun matches(email: String): Boolean
}