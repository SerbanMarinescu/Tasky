package com.example.tasky.feature_agenda.domain.use_case

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tasky.common.Constants.ACTION
import com.example.tasky.common.Constants.EVENT
import com.example.tasky.feature_agenda.data.util.OperationType
import com.example.tasky.feature_agenda.data.worker.EventWorker
import com.example.tasky.feature_agenda.data.worker.enqueueOneTimeWorker
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.util.ErrorType.HTTP
import com.example.tasky.util.ErrorType.IO
import com.example.tasky.util.ErrorType.OTHER
import com.example.tasky.util.Resource
import com.example.tasky.util.Result
import com.squareup.moshi.Moshi

class CreateEvent(
    private val repository: EventRepository,
    private val workManager: WorkManager,
    private val moshi: Moshi
) {

    suspend operator fun invoke(event: AgendaItem.Event): Result<Unit> {
        val result = repository.createEvent(event)

        return when (result) {
            is Resource.Error -> {
                when (result.errorType) {
                    HTTP -> Result.Error(result.message ?: "Unknown Error")

                    IO -> {
                        val jsonEvent = moshi.adapter(AgendaItem.Event::class.java).toJson(event)
                        val inputData = Data.Builder()
                            .putString(ACTION, OperationType.CREATE.toString())
                            .putString(EVENT, jsonEvent)
                            .build()

                        enqueueOneTimeWorker(
                            workManager = workManager,
                            inputData = inputData,
                            requestBuilder = OneTimeWorkRequestBuilder<EventWorker>()
                        )
                        Result.Success()
                    }

                    OTHER -> {
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