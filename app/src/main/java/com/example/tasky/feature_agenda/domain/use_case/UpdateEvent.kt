package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.feature_agenda.domain.util.OperationType
import com.example.tasky.feature_agenda.domain.util.TaskScheduler
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import com.example.tasky.util.Result

class UpdateEvent(
    private val repository: EventRepository,
    private val taskScheduler: TaskScheduler
) {

    suspend operator fun invoke(event: AgendaItem.Event, deletedPhotos: List<Photo> = emptyList()): Result<Unit> {
        val result = repository.updateEvent(event, deletedPhotos)

        return when (result) {
            is Resource.Error -> {
                when (result.errorType) {
                    ErrorType.HTTP -> Result.Error(result.message ?: "Unknown Error")

                    ErrorType.IO -> {
                        taskScheduler.scheduleItemToBeSynced(event, OperationType.UPDATE)
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
