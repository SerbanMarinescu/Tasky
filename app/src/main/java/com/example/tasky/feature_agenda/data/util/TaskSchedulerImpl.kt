package com.example.tasky.feature_agenda.data.util

import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.local.entity.SyncItemEntity
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.feature_agenda.domain.util.OperationType
import com.example.tasky.feature_agenda.domain.util.TaskScheduler

class TaskSchedulerImpl(
    private val db: AgendaDatabase
): TaskScheduler {

    override suspend fun scheduleItemToBeSynced(item: AgendaItem, operation: OperationType) {
        val itemToBeSynced = SyncItemEntity(
            itemId = item.id.toInt(),
            itemType = when(item) {
                is AgendaItem.Event -> AgendaItemType.EVENT
                is AgendaItem.Reminder -> AgendaItemType.REMINDER
                is AgendaItem.Task -> AgendaItemType.TASK
            },
            operation = operation
        )

        db.agendaDao.upsertItemToBeSynced(itemToBeSynced)
    }
}