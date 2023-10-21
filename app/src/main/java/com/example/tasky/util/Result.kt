package com.example.tasky.util

sealed class Result {
    data object Success : Result()
    data class Error(val message: String): Result()
}
