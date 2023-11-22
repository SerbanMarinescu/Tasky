package com.example.tasky.feature_agenda.domain.model

sealed interface EventPhoto {

    data class Remote(
        val key: String,
        val url: String
    ): EventPhoto

    data class Local(
        val key: String,
        val uri: String
    ): EventPhoto
}