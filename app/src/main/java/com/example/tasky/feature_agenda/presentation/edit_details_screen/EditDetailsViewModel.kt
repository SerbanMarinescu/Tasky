package com.example.tasky.feature_agenda.presentation.edit_details_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class EditDetailsViewModel: ViewModel() {

    private val _state = MutableStateFlow(EditDetailsState())
    val state = _state.asStateFlow()

    fun onEvent(event: EditDetailsEvent) {
        when(event) {
            is EditDetailsEvent.ContentChanged -> {
                _state.update {
                    it.copy(content = event.content)
                }
            }

            is EditDetailsEvent.SetPageTitle -> {
                _state.update {
                    it.copy(
                        pageTitle = event.pageTitle
                    )
                }
            }
        }
    }
}