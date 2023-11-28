package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.domain.model.EventPhoto
import com.example.tasky.feature_agenda.domain.util.PhotoValidator

class ValidatePhotos(
    private val photoValidator: PhotoValidator
) {

    suspend operator fun invoke(photoList: MutableList<EventPhoto.Local>): List<EventPhoto.Local> {

        val validPhotos = mutableListOf<EventPhoto.Local>()

        photoList.forEach { photo ->
            val imageBytes = photoValidator.readUriAsByteArray(photo.uri)

            imageBytes?.let { byteArray ->
                val isPhotoSizeValid = photoValidator.checkImageSize(byteArray)

                if(isPhotoSizeValid) {
                    validPhotos.add(photo.copy(byteArray = byteArray))
                } else {
                    val compressedPhoto = photoValidator.compressImage(byteArray, 80)
                    val isPhotoValidAfterCompression = photoValidator.checkImageSize(compressedPhoto)

                    if(isPhotoValidAfterCompression) {
                        validPhotos.add(photo.copy(byteArray = compressedPhoto))
                    }
                }
            }
        }

        return validPhotos
    }
}