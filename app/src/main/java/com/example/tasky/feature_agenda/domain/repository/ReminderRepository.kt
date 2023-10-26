package com.example.tasky.feature_agenda.domain.repository

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.util.Result

interface ReminderRepository {

    suspend fun createReminder(reminder: AgendaItem.Reminder): Result<Unit>

    suspend fun updateReminder(reminder: AgendaItem.Reminder): Result<Unit>

    suspend fun getReminder(reminderId: String): Result<AgendaItem.Reminder>

    suspend fun deleteReminder(reminderId: String): Result<Unit>
}