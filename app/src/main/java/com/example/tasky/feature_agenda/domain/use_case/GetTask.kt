package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.TaskRepository
import com.example.tasky.util.Resource
import com.example.tasky.util.Result

class GetTask(
    private val repository: TaskRepository
) {

    suspend operator fun invoke(taskId: String): Result<AgendaItem.Task> {
        val result = repository.getTask(taskId)

        return when(result) {
            is Resource.Error -> Result.Error(result.message ?: "Unknown Error")
            is Resource.Success -> Result.Success(result.data)
            else -> Result.Error(result.message ?: "Unknown Error")
        }
    }
}