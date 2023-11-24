package com.example.tasky.feature_agenda.domain.util

import java.io.File

interface PhotoValidator {
    suspend fun readUriAsByteArray(imageUriString: String): ByteArray?
    suspend fun saveImageBytes(imageBytes: ByteArray, parentDirectory: File, fileName: String)
    fun checkImageSize(imageBytes: ByteArray): Boolean
    suspend fun compressImage(imageBytes: ByteArray, quality: Int): ByteArray
    suspend fun getDirectoryForImages(nameByEventId: String): File
}