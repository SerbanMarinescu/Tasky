package com.example.tasky.feature_agenda.data.repository

import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toReminder
import com.example.tasky.feature_agenda.data.mapper.toReminderDto
import com.example.tasky.feature_agenda.data.mapper.toReminderEntity
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.ReminderRepository
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import retrofit2.HttpException
import java.io.IOException

class ReminderRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase
): ReminderRepository {

    override suspend fun createReminder(reminder: AgendaItem.Reminder): Resource<Unit> {

        val reminderEntity = reminder.toReminderEntity()
        db.reminderDao.upsertReminder(reminderEntity)

        return syncCreatedReminder(reminder)
    }

    override suspend fun syncCreatedReminder(reminder: AgendaItem.Reminder): Resource<Unit> {
        return try {
            api.createReminder(reminder.toReminderDto())
            Resource.Success()
        } catch(e: HttpException) {
           Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun updateReminder(reminder: AgendaItem.Reminder): Resource<Unit> {

        val reminderEntity = reminder.toReminderEntity()
        db.reminderDao.upsertReminder(reminderEntity)

        return syncUpdatedReminder(reminder)
    }

    override suspend fun syncUpdatedReminder(reminder: AgendaItem.Reminder): Resource<Unit> {
        return try {
            api.updateReminder(reminder.toReminderDto())
            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(e.message ?: "Couldn't reach server", ErrorType.IO)
        }
    }

    override suspend fun getReminder(reminderId: String): Resource<AgendaItem.Reminder> {

        val localReminder = db.reminderDao.getReminderById(reminderId.toInt())

        localReminder?.let {
            return Resource.Success(localReminder.toReminder())
        }

        return try {

            val response = api.getReminder(reminderId)
            val reminder = response.body()?.toReminder() ?: return Resource.Error(message = "No such reminder was found!", errorType = ErrorType.OTHER)

            val reminderEntity = reminder.toReminderEntity()
            db.reminderDao.upsertReminder(reminderEntity)
            val newReminder = db.reminderDao.getReminderById(reminderId.toInt()) ?: return Resource.Error(message = "No such reminder was found!", errorType = ErrorType.OTHER)

            Resource.Success(newReminder.toReminder())
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun deleteReminder(reminder: AgendaItem.Reminder): Resource<Unit> {
        db.reminderDao.deleteReminderById(reminder.reminderId.toInt())
        return syncDeletedReminder(reminder)
    }

    override suspend fun syncDeletedReminder(reminder: AgendaItem.Reminder): Resource<Unit> {
        return try {
            api.deleteReminder(reminder.reminderId)
            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }
}