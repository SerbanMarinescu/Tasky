package com.example.tasky.feature_agenda.data.worker

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

fun enqueueWorker(
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

inline fun <reified T : Enum<T>> Data.getEnum(key: String): T? {
    val value = getString(key) ?: return null
    return enumValueOf<T>(value)
}