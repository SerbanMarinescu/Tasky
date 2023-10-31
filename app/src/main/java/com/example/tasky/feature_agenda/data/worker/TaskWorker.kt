package com.example.tasky.feature_agenda.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tasky.common.Constants.ACTION
import com.example.tasky.common.Constants.TASK
import com.example.tasky.feature_agenda.data.util.OperationType
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.TaskRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
    private val moshi: Moshi,
    private val repository: TaskRepository
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            val action = workerParams.inputData.getEnum<OperationType>(ACTION) ?: return@withContext Result.failure()
            val jsonTask = workerParams.inputData.getString(TASK) ?: return@withContext Result.failure()

            val taskAdapter = moshi.adapter(AgendaItem.Task::class.java)
            val task = taskAdapter.fromJson(jsonTask) ?: return@withContext Result.failure()

            when(action) {
                OperationType.CREATE -> {
                    val result = repository.syncCreatedTask(task)
                    getWorkerResult(result)
                }
                OperationType.UPDATE -> {
                    val result = repository.syncUpdatedTask(task)
                    getWorkerResult(result)
                }
                OperationType.DELETE -> {
                    val result = repository.syncDeletedTask(task)
                    getWorkerResult(result)
                }
            }
        }
    }
}