package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.TaskRepository
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.feature_agenda.domain.util.OperationType
import com.example.tasky.feature_agenda.domain.util.TaskScheduler
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import com.example.tasky.util.Result

class DeleteTask(
    private val repository: TaskRepository,
    private val taskScheduler: TaskScheduler
) {

    suspend operator fun invoke(task: AgendaItem.Task): Result<Unit> {
        val result = repository.deleteTask(task)

        return when (result) {
            is Resource.Error -> {
                when (result.errorType) {
                    ErrorType.HTTP -> Result.Error(result.message ?: "Unknown Error")

                    ErrorType.IO -> {
                        taskScheduler.scheduleItemToBeSynced(task, AgendaItemType.TASK, OperationType.DELETE)
                        Result.Success()
                    }

                    ErrorType.OTHER -> Result.Error(result.message ?: "Unknown Error")

                    null -> Result.Error(result.message ?: "Unknown Error")
                }
            }

            is Resource.Success -> Result.Success()

            else -> Result.Error(result.message ?: "Unknown Error")
        }
    }
}