package com.example.tasky.feature_agenda.data.util

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tasky.common.Constants.ACTION
import com.example.tasky.common.Constants.DELETED_PHOTOS
import com.example.tasky.common.Constants.EVENT
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.local.entity.SyncItemEntity
import com.example.tasky.feature_agenda.data.worker.EventWorker
import com.example.tasky.feature_agenda.data.worker.enqueueOneTimeWorker
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.feature_agenda.domain.util.OperationType
import com.example.tasky.feature_agenda.domain.util.TaskScheduler
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class TaskSchedulerImpl(
    private val workManager: WorkManager,
    private val moshi: Moshi,
    private val db: AgendaDatabase
): TaskScheduler {

    override fun scheduleEventTask(event: AgendaItem.Event, operation: OperationType, deletedPhotos:List<Photo>) {

        val jsonEvent = moshi.adapter(AgendaItem.Event::class.java).toJson(event)
        val jsonDeletedPhotos = moshi.adapter<List<Photo>>(
            Types.newParameterizedType(
                List::class.java,
                Photo::class.java
            )
        ).toJson(deletedPhotos)

        val inputData = Data.Builder()
            .putString(ACTION, operation.name)
            .putString(EVENT, jsonEvent)
            .putString(DELETED_PHOTOS, jsonDeletedPhotos)
            .build()

        enqueueOneTimeWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<EventWorker>()
        )

    }

    override suspend fun scheduleItemToBeSynced(item: AgendaItem, itemType: AgendaItemType, operation: OperationType) {
        when(itemType) {
            AgendaItemType.EVENT -> {
                val event = item as AgendaItem.Event
                val itemToBeSynced = SyncItemEntity(
                    itemId = event.eventId.toInt(),
                    itemType = itemType,
                    operation = operation
                )
                db.agendaDao.upsertItemToBeSynced(itemToBeSynced)
            }
            AgendaItemType.REMINDER -> {
                val reminder = item as AgendaItem.Reminder
                val itemToBeSynced = SyncItemEntity(
                    itemId = reminder.reminderId.toInt(),
                    itemType = itemType,
                    operation = operation
                )
                db.agendaDao.upsertItemToBeSynced(itemToBeSynced)
            }
            AgendaItemType.TASK -> {
                val task = item as AgendaItem.Task
                val itemToBeSynced = SyncItemEntity(
                    itemId = task.taskId.toInt(),
                    itemType = itemType,
                    operation = operation
                )
                db.agendaDao.upsertItemToBeSynced(itemToBeSynced)
            }
        }
    }
}