package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.util.Resource
import com.example.tasky.util.Result

class GetEvent(
    private val repository: EventRepository
) {

    suspend operator fun invoke(eventId: String): Result<AgendaItem.Event> {
        val result = repository.getEvent(eventId)

        return when(result) {
            is Resource.Error -> Result.Error(result.message ?: "Unknown Error")
            is Resource.Success -> Result.Success(result.data)
            else -> Result.Error(result.message ?: "Unknown Error")
        }
    }
}