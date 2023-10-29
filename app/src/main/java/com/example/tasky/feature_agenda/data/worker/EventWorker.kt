package com.example.tasky.feature_agenda.data.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.tasky.common.Constants.ACTION
import com.example.tasky.common.Constants.DELETED_PHOTOS
import com.example.tasky.common.Constants.EVENT
import com.example.tasky.common.Constants.WORK_DATA_KEY
import com.example.tasky.feature_agenda.data.util.ActionType
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import com.example.tasky.util.Result
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
    private val moshi: Moshi,
    private val repository: EventRepository
): CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            val action = workerParams.inputData.getEnum<ActionType>(ACTION) ?: return@withContext Result.failure()
            val jsonEvent = workerParams.inputData.getString(EVENT) ?: return@withContext Result.failure()
            val jsonPhotoList = workerParams.inputData.getString(DELETED_PHOTOS)

            val eventAdapter = moshi.adapter(AgendaItem.Event::class.java)
            val photoAdapter = moshi.adapter<List<Photo>>(Types.newParameterizedType(List::class.java, Photo::class.java))

            val event = eventAdapter.fromJson(jsonEvent) ?: return@withContext Result.failure()
            val deletedPhotos = jsonPhotoList?.let {
                photoAdapter.fromJson(jsonPhotoList)
            } ?: emptyList()

             when(action) {
                 ActionType.CREATE -> {
                     val result = repository.syncCreatedEvent(event)
                     getResult(result)
                 }
                 ActionType.UPDATE -> {
                     val result = repository.syncUpdatedEvent(event, deletedPhotos)
                     getResult(result)
                 }
                 ActionType.DELETE -> {
                     val result = repository.syncDeletedEvent(event)
                     getResult(result)
                 }
             }
        }
    }

    private fun <T> getResult(result: Resource<T>): Result {
        return when(result) {
            is Resource.Error -> {
                when(result.errorType) {
                    ErrorType.HTTP -> {
                        Result.failure(workDataOf(WORK_DATA_KEY to result.message))
                    }
                    ErrorType.IO -> {
                        Result.retry()
                    }
                    ErrorType.OTHER -> {
                        Result.failure(workDataOf(WORK_DATA_KEY to result.message))
                    }
                    null -> {
                        Result.failure(workDataOf(WORK_DATA_KEY to result.message))
                    }
                }
            }
            is Resource.Success -> {
                if(result.data == null) {
                    Result.success()
                } else {
                    Result.success(workDataOf(WORK_DATA_KEY to result.data))
                }
            }
            else -> Result.failure()
        }
    }
}