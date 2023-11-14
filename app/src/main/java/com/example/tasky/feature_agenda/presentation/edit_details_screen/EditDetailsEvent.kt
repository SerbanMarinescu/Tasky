package com.example.tasky.feature_agenda.presentation.edit_details_screen

sealed class EditDetailsEvent {
    data class ContentChanged(val content: String): EditDetailsEvent()
    data class SetPageTitle(val pageTitle: String): EditDetailsEvent()
}
