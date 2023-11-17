package com.example.tasky.feature_agenda.domain.repository

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.util.Resource

interface ReminderRepository {

    suspend fun createReminder(reminder: AgendaItem.Reminder): Resource<Unit>

    suspend fun syncCreatedReminder(reminder: AgendaItem.Reminder): Resource<Unit>

    suspend fun updateReminder(reminder: AgendaItem.Reminder): Resource<Unit>

    suspend fun syncUpdatedReminder(reminder: AgendaItem.Reminder): Resource<Unit>

    suspend fun getReminder(reminderId: String): Resource<AgendaItem.Reminder>

    suspend fun deleteReminder(reminderId: String): Resource<Unit>

    suspend fun syncDeletedReminder(reminderId: String): Resource<Unit>
}