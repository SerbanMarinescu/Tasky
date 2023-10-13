package com.example.tasky.util

import com.example.tasky.feature_authentication.domain.util.UserPreferences
import okhttp3.Interceptor
import okhttp3.Response

class ApiTokenInterceptor(
    private val userPrefs: UserPreferences
) : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {

        val authenticatedUser = userPrefs.getAuthenticatedUser()
        val token = authenticatedUser?.token ?: ""

        return chain.proceed(
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        )
    }
}