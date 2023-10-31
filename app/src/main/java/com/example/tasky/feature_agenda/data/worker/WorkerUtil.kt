package com.example.tasky.feature_agenda.data.worker

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.tasky.common.Constants
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource

fun enqueueOneTimeWorker(
    workManager: WorkManager,
    inputData: Data,
    requestBuilder: OneTimeWorkRequest.Builder
) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val myWorkRequest = requestBuilder
        .setConstraints(constraints)
        .setInputData(inputData)
        .build()

    workManager.enqueue(myWorkRequest)
}

fun enqueuePeriodicWorker(
    workManager: WorkManager,
    inputData: Data,
    requestBuilder: PeriodicWorkRequest.Builder
) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val myWorkRequest = requestBuilder
        .setConstraints(constraints)
        .setInputData(inputData)
        .build()

    workManager.enqueue(myWorkRequest)
}

fun <T> getWorkerResult(result: Resource<T>): ListenableWorker.Result {
    return when(result) {
        is Resource.Error -> {
            when(result.errorType) {
                ErrorType.HTTP -> {
                    ListenableWorker.Result.failure(workDataOf(Constants.WORK_DATA_KEY to result.message))
                }
                ErrorType.IO -> {
                    ListenableWorker.Result.retry()
                }
                ErrorType.OTHER -> {
                    ListenableWorker.Result.failure(workDataOf(Constants.WORK_DATA_KEY to result.message))
                }
                null -> {
                    ListenableWorker.Result.failure()
                }
            }
        }
        is Resource.Success -> {
            if(result.data == null) {
                ListenableWorker.Result.success()
            } else {
                ListenableWorker.Result.success(workDataOf(Constants.WORK_DATA_KEY to result.data))
            }
        }
        else -> ListenableWorker.Result.failure()
    }
}

inline fun <reified T : Enum<T>> Data.getEnum(key: String): T? {
    val value = getString(key) ?: return null
    return enumValueOf<T>(value)
}