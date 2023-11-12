package com.example.tasky.feature_agenda.domain.util

import com.example.tasky.feature_agenda.domain.model.AgendaItem

data class AgendaItemKey(
    val type: AgendaItemType,
    val itemId: String
)

fun AgendaItem.toAgendaItemType(): AgendaItemType {
    return when(this) {
        is AgendaItem.Event -> AgendaItemType.EVENT
        is AgendaItem.Reminder -> AgendaItemType.REMINDER
        is AgendaItem.Task -> AgendaItemType.TASK
    }
}
