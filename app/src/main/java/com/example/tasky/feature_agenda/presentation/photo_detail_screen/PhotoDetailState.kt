package com.example.tasky.feature_agenda.presentation.photo_detail_screen

import android.net.Uri

data class PhotoDetailState(
    val photoUri: Uri? = null,
    val photoKey: String? = null
)
