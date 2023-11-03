package com.example.tasky.feature_agenda.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tasky.common.Constants.ACTION
import com.example.tasky.common.Constants.DELETED_PHOTOS
import com.example.tasky.common.Constants.EVENT
import com.example.tasky.feature_agenda.domain.util.OperationType
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
    private val moshi: Moshi,
    private val repository: EventRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            val action = workerParams.inputData.getEnum<OperationType>(ACTION)
                ?: return@withContext Result.failure()
            val jsonEvent =
                workerParams.inputData.getString(EVENT) ?: return@withContext Result.failure()
            val jsonPhotoList = workerParams.inputData.getString(DELETED_PHOTOS)

            val eventAdapter = moshi.adapter(AgendaItem.Event::class.java)
            val photoAdapter = moshi.adapter<List<Photo>>(
                Types.newParameterizedType(
                    List::class.java,
                    Photo::class.java
                )
            )

            val event = eventAdapter.fromJson(jsonEvent) ?: return@withContext Result.failure()
            val deletedPhotos = jsonPhotoList?.let {
                photoAdapter.fromJson(jsonPhotoList)
            } ?: emptyList()

            if (runAttemptCount > 3) {
                return@withContext Result.failure()
            }

            when (action) {
                OperationType.CREATE -> {
                    val result = repository.syncCreatedEvent(event)
                    getWorkerResult(result)
                }

                OperationType.UPDATE -> {
                    val result = repository.syncUpdatedEvent(event, deletedPhotos)
                    getWorkerResult(result)
                }

                OperationType.DELETE -> {
                    Result.failure()
                }
            }
        }
    }
}