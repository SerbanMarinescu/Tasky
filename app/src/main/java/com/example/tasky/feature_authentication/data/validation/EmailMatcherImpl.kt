package com.example.tasky.feature_authentication.data.validation

import android.util.Patterns
import com.example.tasky.feature_authentication.domain.validation.EmailMatcher

class EmailMatcherImpl: EmailMatcher {
    override fun matches(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}