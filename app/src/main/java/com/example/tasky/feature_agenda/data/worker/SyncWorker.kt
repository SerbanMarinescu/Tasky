package com.example.tasky.feature_agenda.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tasky.common.Constants.DELETED_EVENT_IDS
import com.example.tasky.common.Constants.DELETED_REMINDER_IDS
import com.example.tasky.common.Constants.DELETED_TASK_IDS
import com.example.tasky.feature_agenda.domain.repository.AgendaRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
    private val moshi: Moshi,
    private val repository: AgendaRepository
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            val jsonDeletedEventIds = workerParams.inputData.getString(DELETED_EVENT_IDS) ?: return@withContext Result.failure()
            val jsonDeletedReminderIds = workerParams.inputData.getString(DELETED_REMINDER_IDS) ?: return@withContext Result.failure()
            val jsonDeletedTaskIds = workerParams.inputData.getString(DELETED_TASK_IDS) ?: return@withContext Result.failure()

            val listAdapter = moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))
            val deletedEventIds = listAdapter.fromJson(jsonDeletedEventIds) ?: return@withContext Result.failure()
            val deletedReminderIds = listAdapter.fromJson(jsonDeletedReminderIds) ?: return@withContext Result.failure()
            val deletedTaskIds = listAdapter.fromJson(jsonDeletedTaskIds) ?: return@withContext Result.failure()

            val result = repository.syncAgenda(
                deletedEventIds = deletedEventIds,
                deletedReminderIds = deletedReminderIds,
                deletedTaskIds = deletedTaskIds
            )
            getWorkerResult(result)
        }
    }
}