package com.example.tasky.feature_agenda.data.util

import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.local.entity.SyncItemEntity
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.feature_agenda.domain.util.OperationType
import com.example.tasky.feature_agenda.domain.util.TaskScheduler

class TaskSchedulerImpl(
    private val db: AgendaDatabase
): TaskScheduler {

    override suspend fun scheduleItemToBeSynced(itemId: String, itemType: AgendaItemType, operation: OperationType) {
        val itemToBeSynced = SyncItemEntity(
            itemId = itemId,
            itemType = itemType,
            operation = operation
        )

        db.agendaDao.upsertItemToBeSynced(itemToBeSynced)
    }
}