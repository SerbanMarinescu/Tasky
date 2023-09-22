package com.example.tasky.feature_authentication.data.repository

import android.content.SharedPreferences
import com.example.tasky.feature_authentication.data.remote.TaskyAuthApi
import com.example.tasky.feature_authentication.data.remote.auth.LoginRequest
import com.example.tasky.feature_authentication.data.remote.auth.RegistrationRequest
import com.example.tasky.feature_authentication.data.util.AuthResult
import com.example.tasky.feature_authentication.domain.repository.AuthRepository
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val api: TaskyAuthApi,
    private val prefs: SharedPreferences
) : AuthRepository{
    override suspend fun signUp(fullName: String, email: String, password: String): AuthResult {
        return try {
            api.signUp(RegistrationRequest(
                fullName = fullName,
                email = email,
                password = password
            ))
            signIn(email, password)
        } catch (e: HttpException) {
            if(e.code() == 401){
                AuthResult.Unauthorized(e.message)
            } else {
                AuthResult.Error(e.message)
            }
        } catch (e: Exception){
            AuthResult.Error(e.message)
        }
    }

    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val response = api.signIn(LoginRequest(email, password))
            prefs.edit()
                .putString("jwt", response.body()?.token)
                .apply()
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if(e.code() == 401){
                AuthResult.Unauthorized(e.message)
            } else {
                AuthResult.Error(e.message)
            }
        } catch (e: Exception){
            AuthResult.Error(e.message)
        }
    }

    override suspend fun authenticate(): AuthResult {
        return try {
            val token = prefs.getString("jwt", null) ?: return AuthResult.Error("Jwt Token has expired!")
            api.authenticate("Bearer $token")
            AuthResult.Authorized()
        } catch (e: HttpException){
            if(e.code() == 401){
                AuthResult.Unauthorized(e.message)
            } else {
                AuthResult.Error(e.message)
            }
        } catch (e: Exception){
            AuthResult.Error(e.message)
        }

    }
}