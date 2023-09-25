package com.example.tasky.feature_authentication.domain.use_case

import com.example.tasky.feature_authentication.data.util.AuthResult
import com.example.tasky.feature_authentication.domain.repository.AuthRepository

class Authenticate(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AuthResult {
        return repository.authenticate()
    }
}