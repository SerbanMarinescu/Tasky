package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.ReminderRepository
import com.example.tasky.util.Resource
import com.example.tasky.util.Result

class GetReminder(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(reminderId: String): Result<AgendaItem.Reminder> {
        val result = repository.getReminder(reminderId)

        return when(result) {
            is Resource.Error -> Result.Error(result.message ?: "Unknown Error")
            is Resource.Success -> Result.Success(result.data)
            else -> Result.Error(result.message ?: "Unknown Error")
        }
    }
}