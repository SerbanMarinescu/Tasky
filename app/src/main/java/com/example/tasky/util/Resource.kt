package com.example.tasky.util

sealed class Resource<T>(val data: T? = null, val message: String? = null, val errorType: ErrorType? = null){
    class Success<T>(data: T? = null): Resource<T>(data)
    class Error<T>(message: String, errorType: ErrorType, data: T? = null): Resource<T>(message = message, errorType = errorType, data = data)
    class Loading<T>(val isLoading: Boolean = true): Resource<T>()
}