package com.example.tasky.feature_agenda.domain.util

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Photo

interface TaskScheduler {

    fun scheduleEventTask(event: AgendaItem.Event, operation: OperationType, deletedPhotos: List<Photo> = emptyList())

    suspend fun scheduleItemToBeSynced(item: AgendaItem, itemType: AgendaItemType, operation: OperationType)
}