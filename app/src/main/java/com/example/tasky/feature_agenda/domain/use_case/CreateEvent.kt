package com.example.tasky.feature_agenda.domain.use_case

import android.util.Log
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.feature_agenda.domain.util.OperationType
import com.example.tasky.feature_agenda.domain.util.TaskScheduler
import com.example.tasky.util.ErrorType.HTTP
import com.example.tasky.util.ErrorType.IO
import com.example.tasky.util.ErrorType.OTHER
import com.example.tasky.util.ErrorType.VALIDATION_ERROR
import com.example.tasky.util.Resource
import com.example.tasky.util.Result

class CreateEvent(
    private val repository: EventRepository,
    private val taskScheduler: TaskScheduler
) {

    suspend operator fun invoke(event: AgendaItem.Event): Result<Unit> {
        val result = repository.createEvent(event)

        return when (result) {
            is Resource.Error -> {
                when (result.errorType) {
                    HTTP -> Result.Error(result.message ?: "Unknown Error")

                    IO -> {
                        Log.d("ErrorUploading", "Error: ${result.message}")
                        taskScheduler.scheduleItemToBeSynced(
                            itemId = event.eventId,
                            itemType = AgendaItemType.EVENT,
                            operation = OperationType.CREATE
                        )
                        Result.Success()
                    }

                    VALIDATION_ERROR,OTHER -> {
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