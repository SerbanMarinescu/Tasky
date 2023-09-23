package com.example.tasky.feature_authentication.domain.repository

import com.example.tasky.feature_authentication.data.util.AuthResult

interface AuthRepository {
    suspend fun signUp(fullName: String, email: String, password: String): AuthResult

    suspend fun signIn(email: String, password: String): AuthResult

    suspend fun authenticate(): AuthResult
}