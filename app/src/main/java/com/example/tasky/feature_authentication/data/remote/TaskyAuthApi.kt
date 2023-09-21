package com.example.tasky.feature_authentication.data.remote

import com.example.tasky.feature_authentication.data.remote.auth.LoginRequest
import com.example.tasky.feature_authentication.data.remote.auth.LoginResponse
import com.example.tasky.feature_authentication.data.remote.auth.RegistrationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TaskyAuthApi {
    @POST("/register")
    suspend fun signUp(
        @Header("x-api-key") key: String,
        @Body registrationRequest: RegistrationRequest
    )

    @POST("/login")
    suspend fun signIn(
        @Header("x-api-key") key: String,
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET("/authenticate")
    suspend fun authenticate(
        @Header("x-api-key") key: String,
        @Header("Authorization") token: String
    )
}