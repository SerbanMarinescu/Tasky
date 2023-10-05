package com.example.tasky.util

import okhttp3.Interceptor
import okhttp3.Response

class ApiTokenInterceptor(
    private val token: String?
) : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", token ?: "")
                .build()
        )
    }
}