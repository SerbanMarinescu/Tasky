package com.example.tasky.feature_agenda.presentation.agenda_screen

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.util.AgendaItemKey
import java.time.ZonedDateTime

sealed class AgendaEvent {
    data class SelectDate(val date: ZonedDateTime): AgendaEvent()
    data class SelectDayIndex(val index: Int): AgendaEvent()
    data object ToggleLogoutBtn: AgendaEvent()
    data class ToggleIsDone(val task: AgendaItem.Task): AgendaEvent()
    data object ToggleDeletionDialog: AgendaEvent()
    data object ToggleItemCreationMenu: AgendaEvent()
    data class ToggleIndividualItemMenu(val itemKey: AgendaItemKey, val showIndividualMenu: Boolean): AgendaEvent()
    data class DeleteItem(val item: AgendaItem): AgendaEvent()
    data object SwipeToRefresh: AgendaEvent()
    data object Logout: AgendaEvent()
}
