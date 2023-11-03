package com.example.tasky.feature_agenda.data.repository

import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toEvent
import com.example.tasky.feature_agenda.data.mapper.toReminder
import com.example.tasky.feature_agenda.data.mapper.toTask
import com.example.tasky.feature_agenda.data.mapper.toUtcTimestamp
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.data.remote.request.SyncAgendaRequest
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.AgendaRepository
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
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

    override suspend fun logout(): Resource<Unit> {
        return try {
            api.logout()
            db.agendaDao.clearCache(db)
            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun getAgendaForSpecificDay(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem>> {

        val startOfDay = zonedDateTime.with(LocalTime.MIN).toUtcTimestamp()
        val endOfDay = zonedDateTime.with(LocalTime.MAX).toUtcTimestamp()

        return combine(
            db.eventDao.getEventsForASpecificDay(startOfDay, endOfDay),
            db.reminderDao.getRemindersForSpecificDay(startOfDay, endOfDay),
            db.taskDao.getTasksForSpecificDay(startOfDay, endOfDay)
        ) { events, reminders, tasks ->

            val localEvents = events.map { it.toEvent() }
            val localReminders = reminders.map { it.toReminder() }
            val localTasks = tasks.map { it.toTask() }

            (localEvents + localReminders + localTasks).sortedBy { it.sortDate }
        }
    }

    override suspend fun fetchAgendaFromRemote(zonedDateTime: ZonedDateTime): Resource<Unit> {

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
            return Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch (e: IOException) {
            return Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }

        db.agendaDao.updateAgendaForSpecificDay(db, remoteAgenda, startOfDay, endOfDay)

        return Resource.Success()
    }

    private suspend fun syncCacheWithServer(): Resource<Unit> {

        val remoteAgenda = try {
            val response = api.getFullAgenda()

            val remoteEvents = response.body()?.events?.map { it.toEvent() } ?: emptyList()
            val remoteReminders = response.body()?.reminders?.map { it.toReminder() } ?: emptyList()
            val remoteTasks = response.body()?.tasks?.map { it.toTask() } ?: emptyList()

            remoteEvents + remoteReminders + remoteTasks

        } catch(e: HttpException) {
            return Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            return Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }

        db.agendaDao.updateFullAgenda(db, remoteAgenda)

        return Resource.Success()
    }

    override suspend fun syncAgenda(
        deletedEventIds: List<String>,
        deletedReminderIds: List<String>,
        deletedTaskIds: List<String>
    ): Resource<Unit> {

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
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }
}