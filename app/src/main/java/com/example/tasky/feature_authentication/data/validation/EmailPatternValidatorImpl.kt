package com.example.tasky.feature_authentication.data.validation

import android.util.Patterns
import com.example.tasky.feature_authentication.domain.validation.EmailPatternValidator

class EmailPatternValidatorImpl: EmailPatternValidator {
    override fun isValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}