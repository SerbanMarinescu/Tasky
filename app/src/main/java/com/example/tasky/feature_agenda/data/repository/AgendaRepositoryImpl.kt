package com.example.tasky.feature_agenda.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toAttendeeEntity
import com.example.tasky.feature_agenda.data.mapper.toEvent
import com.example.tasky.feature_agenda.data.mapper.toEventEntity
import com.example.tasky.feature_agenda.data.mapper.toReminder
import com.example.tasky.feature_agenda.data.mapper.toReminderEntity
import com.example.tasky.feature_agenda.data.mapper.toTask
import com.example.tasky.feature_agenda.data.mapper.toTaskEntity
import com.example.tasky.feature_agenda.data.mapper.toUtcTimestamp
import com.example.tasky.feature_agenda.data.remote.request.SyncAgendaRequest
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.AgendaRepository
import com.example.tasky.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalTime
import java.time.ZonedDateTime

class AgendaRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase
) : AgendaRepository {

    override suspend fun logout(): Result<Unit> {

        return try {
            api.logout()
            Result.Success()
        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAgendaForSpecificDay(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem>> {

        val startOfDay = zonedDateTime.with(LocalTime.MIN).toUtcTimestamp()
        val endOfDay = zonedDateTime.with(LocalTime.MAX).toUtcTimestamp()

        return combine(
            db.eventDao.getEventsForSpecificDay(startOfDay, endOfDay),
            db.reminderDao.getRemindersForSpecificDay(startOfDay, endOfDay),
            db.taskDao.getTasksForSpecificDay(startOfDay, endOfDay)) { events, reminders, tasks ->

            val eventWithAttendees = events.map {
                db.eventDao.getEventWithAttendees(it.eventId)
            }

            val localEvents = eventWithAttendees.map { it?.toEvent() }
            val localReminders = reminders.map { it.toReminder() }
            val localTasks = tasks.map { it.toTask() }

            (localEvents + localReminders + localTasks).filterNotNull().sortedBy { it.sortDate }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun fetchAgendaFromRemote(zonedDateTime: ZonedDateTime): Result<Unit> {

        val timeZone = zonedDateTime.zone
        val time = zonedDateTime.toUtcTimestamp()

        val startOfDay = zonedDateTime.with(LocalTime.MIN).toUtcTimestamp()
        val endOfDay = zonedDateTime.with(LocalTime.MAX).toUtcTimestamp()

        val remoteAgenda = try {
            val response = api.getAgenda(
                timeZone = timeZone.toString(),
                time = time
            )

            val remoteEvents = response.body()?.events?.map { it.toEvent() } ?: emptyList()
            val remoteReminders = response.body()?.reminders?.map { it.toReminder() } ?: emptyList()
            val remoteTasks = response.body()?.tasks?.map { it.toTask() } ?: emptyList()

            remoteEvents + remoteTasks + remoteReminders

        } catch (e: HttpException) {
            return Result.Error(e.message ?: "Invalid Response")
        } catch (e: IOException) {
            return Result.Error(e.message ?: "Couldn't reach server")
        }

        db.agendaDao.updateAgendaForSpecificDay(db, remoteAgenda, startOfDay, endOfDay)

        return Result.Success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun syncCacheWithServer(): Result<Unit> {

        val remoteAgenda = try {
            val response = api.getFullAgenda()

            val remoteEvents = response.body()?.events?.map { it.toEvent() } ?: emptyList()
            val remoteReminders = response.body()?.reminders?.map { it.toReminder() } ?: emptyList()
            val remoteTasks = response.body()?.tasks?.map { it.toTask() } ?: emptyList()

            remoteEvents + remoteReminders + remoteTasks

        } catch(e: HttpException) {
            return Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            return Result.Error(e.message ?: "Couldn't reach server")
        }

        db.agendaDao.updateFullAgenda(db, remoteAgenda)

        return Result.Success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun syncAgenda(
        deletedEventIds: List<String>,
        deletedReminderIds: List<String>,
        deletedTaskIds: List<String>
    ): Result<Unit> {

        return try {
            api.syncAgenda(
                SyncAgendaRequest(
                deletedEventIds = deletedEventIds,
                deletedReminderIds = deletedReminderIds,
                deletedTaskIds = deletedTaskIds
            )
            )
            syncCacheWithServer()
        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }
}