package com.example.tasky.util

sealed class Resource<T>(val data: T? = null, val message: String? = null){
    class Success<T>(data: T? = null): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(message = message, data = data)
    class Loading<T>(val isLoading: Boolean = true): Resource<T>()
}