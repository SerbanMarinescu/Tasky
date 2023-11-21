package com.example.tasky.feature_agenda.presentation.photo_detail_screen

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.tasky.util.ArgumentTypeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _state = MutableStateFlow(PhotoDetailState())
    val state = _state.asStateFlow()

    init {
        val photoUrl = savedStateHandle.get<String>(ArgumentTypeEnum.PHOTO_URL.name)
        val photoUri = Uri.decode(photoUrl).toUri()

        _state.update {
            it.copy(photoUri = photoUri)
        }
    }

    fun onEvent(event: PhotoDetailEvent) {
        when(event) {
            is PhotoDetailEvent.DeletePhoto -> {
                val photoKey = savedStateHandle.get<String>(ArgumentTypeEnum.PHOTO_KEY.name)
                _state.update {
                    it.copy(photoKey = photoKey)
                }
            }
        }
    }
}