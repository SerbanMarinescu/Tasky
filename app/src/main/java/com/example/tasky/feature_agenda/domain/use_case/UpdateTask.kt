package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.TaskRepository
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.feature_agenda.domain.util.OperationType
import com.example.tasky.feature_agenda.domain.util.TaskScheduler
import com.example.tasky.util.ErrorType.HTTP
import com.example.tasky.util.ErrorType.IO
import com.example.tasky.util.ErrorType.OTHER
import com.example.tasky.util.ErrorType.VALIDATION_ERROR
import com.example.tasky.util.Resource
import com.example.tasky.util.Result

class UpdateTask(
    private val repository: TaskRepository,
    private val taskScheduler: TaskScheduler
) {

    suspend operator fun invoke(task: AgendaItem.Task): Result<Unit> {
        val result = repository.updateTask(task)

        return when (result) {
            is Resource.Error -> {
                when (result.errorType) {
                    HTTP -> Result.Error(result.message ?: "Unknown Error")

                    IO -> {
                        taskScheduler.scheduleItemToBeSynced(
                            itemId = task.taskId,
                            itemType = AgendaItemType.TASK,
                            operation = OperationType.UPDATE
                        )
                        Result.Success()
                    }

                    VALIDATION_ERROR,OTHER -> Result.Error(result.message ?: "Unknown Error")

                    null -> Result.Error(result.message ?: "Unknown Error")

                }
            }

            is Resource.Success -> Result.Success()

            else -> Result.Error(result.message ?: "Unknown Error")
        }
    }
}