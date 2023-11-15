package com.example.tasky.feature_agenda.presentation.edit_details_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.tasky.R
import com.example.tasky.feature_authentication.presentation.util.UiText
import com.example.tasky.util.ArgumentTypeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EditDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(EditDetailsState())
    val state = _state.asStateFlow()

    init {
        Log.d("NAV", "Edit details viewModel initialized")
        val type = savedStateHandle.get<String>(ArgumentTypeEnum.TYPE.name) ?: ""
        val text = savedStateHandle.get<String>(ArgumentTypeEnum.TEXT.name) ?: ""
        Log.d("NAV", "Values from savedStateHandle type: $type")
        Log.d("NAV", "Values from savedStateHandle text: $text")

        _state.update {
            it.copy(
                pageTitle = when (type) {
                    ArgumentTypeEnum.TITLE.name -> UiText.StringResource(R.string.EditTitle)
                    ArgumentTypeEnum.DESCRIPTION.name -> UiText.StringResource(R.string.EditDescription)
                    else -> null
                },
                content = text
            )
        }
    }

    fun onEvent(event: EditDetailsEvent) {
        when (event) {
            is EditDetailsEvent.ContentChanged -> {
                _state.update {
                    it.copy(content = event.content)
                }
            }

            EditDetailsEvent.SaveChangedState -> {
                savedStateHandle[ArgumentTypeEnum.TEXT.name] = state.value.content
            }
        }
    }
}