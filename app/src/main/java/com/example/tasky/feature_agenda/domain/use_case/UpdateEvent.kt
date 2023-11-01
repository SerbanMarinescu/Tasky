package com.example.tasky.feature_agenda.domain.use_case

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tasky.common.Constants.ACTION
import com.example.tasky.common.Constants.DELETED_PHOTOS
import com.example.tasky.common.Constants.EVENT
import com.example.tasky.feature_agenda.data.util.OperationType
import com.example.tasky.feature_agenda.data.worker.EventWorker
import com.example.tasky.feature_agenda.data.worker.enqueueOneTimeWorker
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import com.example.tasky.util.Result
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class UpdateEvent(
    private val repository: EventRepository,
    private val workManager: WorkManager,
    private val moshi: Moshi
) {

    suspend operator fun invoke(event: AgendaItem.Event, deletedPhotos: List<Photo> = emptyList()): Result<Unit> {
        val result = repository.updateEvent(event)

        return when (result) {
            is Resource.Error -> {
                when (result.errorType) {
                    ErrorType.HTTP -> Result.Error(result.message ?: "Unknown Error")

                    ErrorType.IO -> {
                        val jsonEvent = moshi.adapter(AgendaItem.Event::class.java).toJson(event)
                        val jsonPhotoList = moshi.adapter<List<Photo>>(
                            Types.newParameterizedType(
                                List::class.java,
                                Photo::class.java
                            )
                        ).toJson(deletedPhotos)
                        val inputData = Data.Builder()
                            .putString(ACTION, OperationType.UPDATE.toString())
                            .putString(EVENT, jsonEvent)
                            .putString(DELETED_PHOTOS, jsonPhotoList)
                            .build()

                        enqueueOneTimeWorker(
                            workManager = workManager,
                            inputData = inputData,
                            requestBuilder = OneTimeWorkRequestBuilder<EventWorker>()
                        )
                        Result.Success()
                    }

                    ErrorType.OTHER -> {
                        Result.Error(result.message ?: "Unknown Error")
                    }

                    null -> Result.Error(result.message ?: "Unknown Error")
                }
            }

            is Resource.Success -> Result.Success()

            else -> Result.Error("Unknown Error")
        }
    }
}
