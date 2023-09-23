package com.example.tasky.feature_authentication.domain.validation

interface UserDataValidator {
    fun validateFullName(fullName: String): Boolean
    fun validateEmail(email: String): Boolean
    fun validatePassword(password: String): Boolean
    fun specifyError() : String
}