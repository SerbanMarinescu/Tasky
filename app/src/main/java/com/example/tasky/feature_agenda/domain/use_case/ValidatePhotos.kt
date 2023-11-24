package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.domain.model.EventPhoto
import com.example.tasky.feature_agenda.domain.util.PhotoValidator

class ValidatePhotos(
    private val photoValidator: PhotoValidator
) {

    suspend operator fun invoke(eventId: String, photoList: MutableList<EventPhoto.Local>) {
        val parentDirectory = photoValidator.getDirectoryForImages(eventId)
        val iterator = photoList.iterator()
        var index = 0

        while (iterator.hasNext()) {
            val photo = iterator.next()
            val imageBytes = photoValidator.readUriAsByteArray(photo.uri)
            imageBytes?.let {
                val isPhotoSizeValid = photoValidator.checkImageSize(imageBytes)

                if(isPhotoSizeValid) {
                    photoValidator.saveImageBytes(
                        imageBytes = imageBytes,
                        parentDirectory = parentDirectory,
                        fileName = "photo$index"
                    )
                    index++
                } else {
                    val compressedImage = photoValidator.compressImage(imageBytes, 80)
                    val isPhotoValidAfterCompression = photoValidator.checkImageSize(compressedImage)

                    if(isPhotoValidAfterCompression) {
                        photoValidator.saveImageBytes(
                            imageBytes = imageBytes,
                            parentDirectory = parentDirectory,
                            fileName = "photo$index"
                        )
                        index++
                    } else {
                        photoList.remove(photo)
                    }
                }
            }
        }
    }
}