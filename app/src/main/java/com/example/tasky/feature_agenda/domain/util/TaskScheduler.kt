package com.example.tasky.feature_agenda.domain.util

interface TaskScheduler {
    suspend fun scheduleItemToBeSynced(itemId: String, itemType: AgendaItemType, operation: OperationType)
}