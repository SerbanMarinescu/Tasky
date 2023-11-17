package com.example.tasky.feature_agenda.presentation.edit_details_screen

import com.example.tasky.feature_authentication.presentation.util.UiText

data class EditDetailsState(
    val pageTitle: UiText? = null,
    val content: String = "",
    val contentSaved: Boolean = false,
    val type: String = ""
)
