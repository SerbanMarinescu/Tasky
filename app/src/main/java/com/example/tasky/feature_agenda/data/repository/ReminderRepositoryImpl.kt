package com.example.tasky.feature_agenda.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toReminder
import com.example.tasky.feature_agenda.data.mapper.toReminderDto
import com.example.tasky.feature_agenda.data.mapper.toReminderEntity
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.ReminderRepository
import com.example.tasky.util.Result
import retrofit2.HttpException
import java.io.IOException

class ReminderRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase
): ReminderRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createReminder(reminder: AgendaItem.Reminder): Result<Unit> {

        return try {

            api.createReminder(reminder.toReminderDto())

            val reminderEntity = reminder.toReminderEntity()
            db.reminderDao.upsertReminder(reminderEntity)

            Result.Success()
        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateReminder(reminder: AgendaItem.Reminder): Result<Unit> {

        return try {

            api.updateReminder(reminder.toReminderDto())

            val reminderEntity = reminder.toReminderEntity()
            db.reminderDao.upsertReminder(reminderEntity)

            Result.Success()
        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getReminder(reminderId: String): Result<AgendaItem.Reminder> {

        val localReminder = db.reminderDao.getReminder(reminderId.toInt())

        localReminder?.let {
            return Result.Success(localReminder.toReminder())
        }

        return try {

            val response = api.getReminder(reminderId)

            val reminder = response.body()?.toReminder() ?: return Result.Error("No such reminder was found!")
            val reminderEntity = reminder.toReminderEntity()

            db.reminderDao.upsertReminder(reminderEntity)

            val newReminder = db.reminderDao.getReminder(reminderId.toInt()) ?: return Result.Error("No such reminder was found!")

            Result.Success(newReminder.toReminder())
        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }

    override suspend fun deleteReminder(reminderId: String): Result<Unit> {

        db.reminderDao.deleteReminder(reminderId.toInt())

        return try {

            api.deleteReminder(reminderId)

            Result.Success()
        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }
}