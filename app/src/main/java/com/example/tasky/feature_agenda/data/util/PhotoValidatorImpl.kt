package com.example.tasky.feature_agenda.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import com.example.tasky.feature_agenda.domain.util.PhotoValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class PhotoValidatorImpl(
    private val context: Context
): PhotoValidator {

    override suspend fun readUriAsByteArray(imageUriString: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            val imageUri = imageUriString.toUri()
            val imageBytes = context.contentResolver.openInputStream(imageUri)?.use {
                it.readBytes()
            }
            imageBytes
        }
    }

    override fun checkImageSize(imageBytes: ByteArray): Boolean {
        return imageBytes.size < 1024 * 1024
    }

    override suspend fun compressImage(imageBytes: ByteArray, quality: Int): ByteArray {
        return withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            outputStream.toByteArray()
        }
    }
}