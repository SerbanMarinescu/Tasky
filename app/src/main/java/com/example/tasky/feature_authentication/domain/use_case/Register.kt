package com.example.tasky.feature_authentication.domain.use_case

import com.example.tasky.feature_authentication.domain.repository.AuthRepository
import com.example.tasky.feature_authentication.domain.validation.EmailMatcher

class Register(
    private val repository: AuthRepository,
    private val matcher: EmailMatcher
) {
    suspend operator fun invoke(fullName: String, email: String, password: String) {
        if(validateUserData(fullName, email, password)) {
            repository.signUp(fullName, email, password)
        }
    }

    private fun validateUserData(fullName: String, email: String, password: String): Boolean {
        if(fullName.isBlank() || email.isBlank() || password.isBlank()) {
            return false
        }

        if(fullName.length < 4 || fullName.length > 50) {
            return false
        }

        if(!matcher.matches(email)){
            return false
        }

        if(!password.contains(Regex("[a-z]")) ||
            !password.contains(Regex("[A-Z]")) ||
            !password.contains(Regex("\\d"))
            ) {
            return false
        }

        if(password.length < 9) {
            return false
        }

        return true
    }
}
