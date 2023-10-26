package com.example.tasky.feature_authentication.data.repository

import com.example.tasky.feature_authentication.data.remote.TaskyAuthApi
import com.example.tasky.feature_authentication.data.remote.auth.LoginRequest
import com.example.tasky.feature_authentication.data.remote.auth.RegistrationRequest
import com.example.tasky.feature_authentication.data.util.AuthResult
import com.example.tasky.feature_authentication.domain.model.AuthenticatedUser
import com.example.tasky.feature_authentication.domain.repository.AuthRepository
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val api: TaskyAuthApi,
    private val userPreferences: UserPreferences
) : AuthRepository {
    override suspend fun signUp(fullName: String, email: String, password: String): AuthResult {
        return try {
            api.signUp(
                RegistrationRequest(
                    fullName = fullName,
                    email = email,
                    password = password
                )
            )
            signIn(email, password)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized(e.message)
            } else {
                AuthResult.Error(e.message)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            AuthResult.Error(e.message)
        }
    }

    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val response = api.signIn(LoginRequest(email, password))

            userPreferences.saveAuthenticatedUser(
                AuthenticatedUser(
                    fullName = response.body()?.fullName ?: return AuthResult.Error("Error Singing In"),
                    email = email,
                    token = response.body()?.token ?: return AuthResult.Error("Error Singing In"),
                    userId = response.body()?.userId ?: return AuthResult.Error("Error Singing In")
                )
            )
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized(e.message)
            } else {
                AuthResult.Error(e.message)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            AuthResult.Error(e.message)
        }
    }

    override suspend fun authenticate(): AuthResult {
        return try {
            //val token = userPreferences.getAuthenticatedUser()?.token ?: return AuthResult.Error("Jwt Token has expired!")
            api.authenticate()
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized(e.message)
            } else {
                AuthResult.Error(e.message)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            AuthResult.Error(e.message)
        }

    }
}