package com.example.tasky.feature_agenda.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tasky.common.Constants.ACTION
import com.example.tasky.common.Constants.REMINDER
import com.example.tasky.feature_agenda.data.util.OperationType
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.ReminderRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
    private val moshi: Moshi,
    private val repository: ReminderRepository
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            val action = workerParams.inputData.getEnum<OperationType>(ACTION) ?: return@withContext Result.failure()
            val jsonReminder = workerParams.inputData.getString(REMINDER) ?: return@withContext Result.failure()

            val reminderAdapter = moshi.adapter(AgendaItem.Reminder::class.java)
            val reminder = reminderAdapter.fromJson(jsonReminder) ?: return@withContext Result.failure()

            when(action) {
                OperationType.CREATE -> {
                    val result = repository.syncCreatedReminder(reminder)
                    getWorkerResult(result)
                }
                OperationType.UPDATE -> {
                    val result = repository.syncUpdatedReminder(reminder)
                    getWorkerResult(result)
                }
                OperationType.DELETE -> {
                    val result = repository.syncDeletedReminder(reminder)
                    getWorkerResult(result)
                }
            }
        }
    }
}