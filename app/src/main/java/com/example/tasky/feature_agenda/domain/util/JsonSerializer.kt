package com.example.tasky.feature_agenda.domain.util

interface JsonSerializer {
    fun <T> fromJson(json: String, clazz: Class<T>): T?
    fun <T> toJson(obj: T, clazz: Class<T>): String
}