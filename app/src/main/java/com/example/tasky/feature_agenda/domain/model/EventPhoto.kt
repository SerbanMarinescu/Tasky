package com.example.tasky.feature_agenda.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface EventPhoto: Parcelable {

    @Parcelize
    data class Remote(
        val key: String,
        val url: String
    ): EventPhoto, Parcelable

    @Parcelize
    data class Local(
        val key: String,
        val uri: String,
        val byteArray: ByteArray
    ): EventPhoto, Parcelable
}