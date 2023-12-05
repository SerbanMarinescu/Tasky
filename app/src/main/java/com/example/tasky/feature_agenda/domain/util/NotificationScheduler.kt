package com.example.tasky.feature_agenda.domain.util

import com.example.tasky.feature_agenda.domain.model.AgendaItem

interface NotificationScheduler {
    fun scheduleNotification(item: AgendaItem)
    fun cancelNotification(itemId: String)
}