package com.example.tasky.feature_agenda.domain.util

sealed class AgendaResult {
    data object Success : AgendaResult()
    data class Error(val message: String): AgendaResult()
}
