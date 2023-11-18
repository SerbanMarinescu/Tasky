package com.example.tasky.feature_agenda.presentation.photo_detail_screen

sealed class PhotoDetailEvent {
    data object DeletePhoto: PhotoDetailEvent()
}
