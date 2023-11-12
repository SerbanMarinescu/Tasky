package com.example.tasky.feature_agenda.presentation.agenda_screen

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.util.AgendaItemKey
import java.time.ZonedDateTime

sealed class AgendaEvent {
    data class SelectDate(val date: ZonedDateTime): AgendaEvent()
    data class SelectDayIndex(val index: Int): AgendaEvent()
    data class ToggleLogoutBtn(val showOption: Boolean): AgendaEvent()
    data class ToggleIsDone(val isDone: Boolean): AgendaEvent()
    data class ToggleItemCreationMenu(val showMenu: Boolean): AgendaEvent()
    data class ToggleIndividualItemMenu(val itemKey: AgendaItemKey, val showIndividualMenu: Boolean): AgendaEvent()
    data class DeleteItem(val item: AgendaItem): AgendaEvent()
    data object SwipeToRefresh: AgendaEvent()
    data object Logout: AgendaEvent()
}
