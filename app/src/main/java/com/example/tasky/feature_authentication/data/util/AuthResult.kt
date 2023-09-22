package com.example.tasky.feature_authentication.data.util

sealed class AuthResult(val message: String? = null) {
    class Authorized: AuthResult()
    class Unauthorized(message: String? = null): AuthResult(message)
    class Error(message: String? = null): AuthResult(message)
}