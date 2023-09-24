package com.example.tasky.feature_authentication.data.validation

import android.util.Patterns
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator

class UserDataValidatorImpl : UserDataValidator {

    private var errorMsg: String = ""
    override fun validateFullName(fullName: String): Boolean {
        if(fullName.isBlank()) {
            errorMsg = "Full Name cannot be empty"
            return false
        }

        if(fullName.length !in 4..50) {
            errorMsg = "Full Name must be between 4 and 50 characters"
            return false
        }

        return true
    }

    override fun validateEmail(email: String): Boolean {
        if(email.isBlank()) {
            errorMsg = "Email cannot be empty"
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMsg = "Email must be valid"
            return false
        }

        return true
    }

    override fun validatePassword(password: String): Boolean {
        if(password.isBlank()) {
            errorMsg = "Password cannot be empty"
            return false
        }

        if(!password.contains(Regex("[a-z]")) ||
            !password.contains(Regex("[A-Z]")) ||
            !password.contains(Regex("\\d"))
        ) {
            errorMsg = "Password must contain lowercase and uppercase letters as well as at least one digit"
            return false
        }

        if(password.length < 9) {
            errorMsg = "Password must be at least 9 characters long"
            return false
        }

        return true
    }

    override fun specifyError(): String {
        return errorMsg
    }
}