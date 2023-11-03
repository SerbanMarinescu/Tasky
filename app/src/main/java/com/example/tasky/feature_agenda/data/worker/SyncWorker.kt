package com.example.tasky.feature_agenda.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toEvent
import com.example.tasky.feature_agenda.data.mapper.toReminder
import com.example.tasky.feature_agenda.data.mapper.toTask
import com.example.tasky.feature_agenda.domain.repository.AgendaRepositories
import com.example.tasky.feature_agenda.domain.util.AgendaItemType.EVENT
import com.example.tasky.feature_agenda.domain.util.AgendaItemType.REMINDER
import com.example.tasky.feature_agenda.domain.util.AgendaItemType.TASK
import com.example.tasky.feature_agenda.domain.util.OperationType
import kotlinx.coroutines.flow.firstOrNull

class SyncWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
    private val repositories: AgendaRepositories,
    private val db: AgendaDatabase
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {

        val syncItems = db.agendaDao.getItemsToBeSynced().firstOrNull()

        syncItems?.forEach { item ->
            val result = when (item.itemType) {
                EVENT -> {
                    val eventEntity = db.eventDao.getEventById(item.itemId)
                    val event = eventEntity?.toEvent()

                    event?.let {
                        when (item.operation) {
                            OperationType.CREATE -> {
                                val result = repositories.eventRepository.syncCreatedEvent(event)
                                getWorkerResult(result)
                            }
                            OperationType.UPDATE -> {
                                val result = repositories.eventRepository.syncUpdatedEvent(event)
                                getWorkerResult(result)
                            }
                            OperationType.DELETE -> {
                                val result = repositories.eventRepository.syncDeletedEvent(event)
                                getWorkerResult(result)
                            }
                        }
                    } ?: Result.failure()
                }

                REMINDER -> {
                    val reminderEntity = db.reminderDao.getReminderById(item.itemId)
                    val reminder = reminderEntity?.toReminder()

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
                    val taskEntity = db.taskDao.getTaskById(item.itemId)
                    val task = taskEntity?.toTask()

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
                db.agendaDao.deleteSyncedItem(item)
            }
        }

        return if(syncItems.isNullOrEmpty()) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}