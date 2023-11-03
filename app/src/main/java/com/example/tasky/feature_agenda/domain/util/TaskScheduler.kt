package com.example.tasky.feature_agenda.domain.util

import com.example.tasky.feature_agenda.domain.model.AgendaItem

interface TaskScheduler {
    suspend fun scheduleItemToBeSynced(item: AgendaItem, operation: OperationType)
}