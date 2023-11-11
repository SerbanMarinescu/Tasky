package com.example.tasky.feature_agenda.presentation.agenda_screen

import java.time.ZonedDateTime

sealed class AgendaEvent {
    data class SelectDate(val date: ZonedDateTime): AgendaEvent()
    data class SelectDayIndex(val index: Int): AgendaEvent()
    data class ToggleLogoutBtn(val showOption: Boolean): AgendaEvent()
    data object SwipeToRefresh: AgendaEvent()
    data object Logout: AgendaEvent()
}
