package com.example.tasky.feature_authentication.domain.use_case

import com.example.tasky.feature_authentication.data.util.AuthResult
import com.example.tasky.feature_authentication.domain.repository.AuthRepository
import com.example.tasky.feature_authentication.domain.util.AuthUseCaseResult

class Authenticate(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AuthUseCaseResult {
        val response = repository.authenticate()

        return when(response) {
            is AuthResult.Authorized -> AuthUseCaseResult.Success
            is AuthResult.Error -> AuthUseCaseResult.GenericError(response.message ?: "Something went wrong")
            is AuthResult.Unauthorized -> AuthUseCaseResult.GenericError(response.message ?: "Something went wrong")
        }
    }
}