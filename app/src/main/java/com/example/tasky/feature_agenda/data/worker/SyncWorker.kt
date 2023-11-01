package com.example.tasky.feature_agenda.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.util.OperationType
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.AgendaRepositories
import com.example.tasky.feature_agenda.domain.util.AgendaItemType.EVENT
import com.example.tasky.feature_agenda.domain.util.AgendaItemType.REMINDER
import com.example.tasky.feature_agenda.domain.util.AgendaItemType.TASK
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull

class SyncWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
    private val moshi: Moshi,
    private val repositories: AgendaRepositories,
    private val db: AgendaDatabase
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {

        db.agendaDao.getItemsToBeSynced().collectLatest { entityItemList ->
            entityItemList.forEach { item ->

               val result = when (item.itemType) {
                    EVENT -> {
                        val event = moshi.adapter(AgendaItem.Event::class.java).fromJson(item.agendaItem)
                        event?.let {
                            when (item.operation) {
                                OperationType.CREATE -> Result.failure()
                                OperationType.UPDATE -> Result.failure()
                                OperationType.DELETE -> {
                                    val result = repositories.eventRepository.syncDeletedEvent(event)
                                    getWorkerResult(result)
                                }
                            }
                        } ?: Result.failure()
                    }

                    REMINDER -> {
                        val reminder = moshi.adapter(AgendaItem.Reminder::class.java).fromJson(item.agendaItem)
                        reminder?.let {
                             when (item.operation) {
                                OperationType.CREATE -> {
                                    val result = repositories.reminderRepository.syncCreatedReminder(reminder)
                                    getWorkerResult(result)
                                }

                                OperationType.UPDATE -> {
                                    val result = repositories.reminderRepository.syncUpdatedReminder(reminder)
                                    getWorkerResult(result)
                                }

                                OperationType.DELETE -> {
                                    val result = repositories.reminderRepository.syncDeletedReminder(reminder)
                                    getWorkerResult(result)
                                }
                            }
                        } ?: Result.failure()
                    }

                    TASK -> {
                        val task = moshi.adapter(AgendaItem.Task::class.java).fromJson(item.agendaItem)
                        task?.let {
                            when (item.operation) {
                                OperationType.CREATE -> {
                                    val result = repositories.taskRepository.syncCreatedTask(task)
                                    getWorkerResult(result)
                                }

                                OperationType.UPDATE -> {
                                    val result = repositories.taskRepository.syncUpdatedTask(task)
                                    getWorkerResult(result)
                                }

                                OperationType.DELETE -> {
                                    val result = repositories.taskRepository.syncDeletedTask(task)
                                    getWorkerResult(result)
                                }
                            }
                        } ?: Result.failure()
                    }
                }

                if(result is Result.Success) {
                    db.agendaDao.itemWasSynced(item)
                }
            }
        }

        val itemList = db.agendaDao.getItemsToBeSynced().firstOrNull()

        return if(itemList.isNullOrEmpty()) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}