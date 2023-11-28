package com.example.tasky.feature_agenda.domain.util

interface PhotoValidator {
    suspend fun readUriAsByteArray(imageUriString: String): ByteArray?
    fun checkImageSize(imageBytes: ByteArray): Boolean
    suspend fun compressImage(imageBytes: ByteArray, quality: Int): ByteArray
}