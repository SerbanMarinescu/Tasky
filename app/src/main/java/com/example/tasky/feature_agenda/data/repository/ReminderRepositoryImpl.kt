package com.example.tasky.feature_agenda.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tasky.common.Constants.ACTION
import com.example.tasky.common.Constants.REMINDER
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toReminder
import com.example.tasky.feature_agenda.data.mapper.toReminderDto
import com.example.tasky.feature_agenda.data.mapper.toReminderEntity
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.data.util.ActionType
import com.example.tasky.feature_agenda.data.worker.ReminderWorker
import com.example.tasky.feature_agenda.data.worker.enqueueWorker
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.ReminderRepository
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.io.IOException

class ReminderRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase,
    private val workManager: WorkManager,
    private val moshi: Moshi
): ReminderRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createReminder(reminder: AgendaItem.Reminder) {

        val reminderEntity = reminder.toReminderEntity()
        db.reminderDao.upsertReminder(reminderEntity)

        val jsonReminder = moshi.adapter(AgendaItem.Reminder::class.java).toJson(reminder)

        val inputData = Data.Builder()
            .putString(ACTION, ActionType.CREATE.toString())
            .putString(REMINDER, jsonReminder)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<ReminderWorker>()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateReminder(reminder: AgendaItem.Reminder) {

        val reminderEntity = reminder.toReminderEntity()
        db.reminderDao.upsertReminder(reminderEntity)

        val jsonReminder = moshi.adapter(AgendaItem.Reminder::class.java).toJson(reminder)

        val inputData = Data.Builder()
            .putString(ACTION, ActionType.UPDATE.toString())
            .putString(REMINDER, jsonReminder)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<ReminderWorker>()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getReminder(reminderId: String): Resource<AgendaItem.Reminder> {

        val localReminder = db.reminderDao.getReminder(reminderId.toInt())

        localReminder?.let {
            return Resource.Success(localReminder.toReminder())
        }

        return try {

            val response = api.getReminder(reminderId)
            val reminder = response.body()?.toReminder() ?: return Resource.Error(message = "No such reminder was found!", errorType = ErrorType.OTHER)

            val reminderEntity = reminder.toReminderEntity()
            db.reminderDao.upsertReminder(reminderEntity)
            val newReminder = db.reminderDao.getReminder(reminderId.toInt()) ?: return Resource.Error(message = "No such reminder was found!", errorType = ErrorType.OTHER)

            Resource.Success(newReminder.toReminder())
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun deleteReminder(reminder: AgendaItem.Reminder) {

        db.reminderDao.deleteReminder(reminder.reminderId.toInt())

        val jsonReminder = moshi.adapter(AgendaItem.Reminder::class.java).toJson(reminder)

        val inputData = Data.Builder()
            .putString(ACTION, ActionType.DELETE.toString())
            .putString(REMINDER, jsonReminder)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<ReminderWorker>()
        )
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