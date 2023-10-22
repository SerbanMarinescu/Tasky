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
import com.example.tasky.feature_agenda.data.remote.SyncAgendaRequest
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.AgendaRepository
import com.example.tasky.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.TimeZone

class AgendaRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase
) : AgendaRepository {

    override suspend fun logout(): Result {

        return try {
            api.logout()
            Result.Success
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

            val localEvents = eventWithAttendees.map { it.toEvent() }
            val localReminders = reminders.map { it.toReminder() }
            val localTasks = tasks.map { it.toTask() }

            (localEvents + localReminders + localTasks).sortedBy { it.sortDate }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun fetchAgendaFromRemote(zonedDateTime: ZonedDateTime): Result {

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

        db.eventDao.deleteEventsForSpecificDay(startOfDay, endOfDay)
        db.eventDao.deleteAttendeesForSpecificDay(startOfDay, endOfDay)
        db.reminderDao.deleteRemindersForSpecificDay(startOfDay, endOfDay)
        db.taskDao.deleteTasksForSpecificDay(startOfDay, endOfDay)

            val eventList = remoteAgenda.filterIsInstance<AgendaItem.Event>()
            val remindersList = remoteAgenda.filterIsInstance<AgendaItem.Reminder>()
            val tasksList = remoteAgenda.filterIsInstance<AgendaItem.Task>()

            eventList.forEach { event->
                val eventEntity = event.toEventEntity()
                val attendeeEntities = event.attendees.map { it.toAttendeeEntity(event.eventId) }

                db.eventDao.upsertEvent(eventEntity)
                db.eventDao.upsertAttendees(attendeeEntities)
            }

            tasksList.forEach { task ->
                val taskEntity = task.toTaskEntity()
                db.taskDao.upsertTask(taskEntity)
            }

            remindersList.forEach { reminder ->
                val reminderEntity = reminder.toReminderEntity()
                db.reminderDao.upsertReminder(reminderEntity)
            }

            return Result.Success
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun syncCacheWithServer(): Result {

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

            db.eventDao.deleteEvents()
            db.eventDao.deleteAttendees()
            db.reminderDao.deleteReminders()
            db.taskDao.deleteTasks()

            val eventList = remoteAgenda.filterIsInstance<AgendaItem.Event>()
            val remindersList = remoteAgenda.filterIsInstance<AgendaItem.Reminder>()
            val tasksList = remoteAgenda.filterIsInstance<AgendaItem.Task>()

            eventList.forEach { event ->
                val eventEntity = event.toEventEntity()
                val attendeeEntities = event.attendees.map { it.toAttendeeEntity(event.eventId) }

                db.eventDao.upsertEvent(eventEntity)
                db.eventDao.upsertAttendees(attendeeEntities)
            }

            tasksList.forEach { task ->
                val taskEntity = task.toTaskEntity()
                db.taskDao.upsertTask(taskEntity)
            }

            remindersList.forEach { reminder ->
                val reminderEntity = reminder.toReminderEntity()
                db.reminderDao.upsertReminder(reminderEntity)
            }

        return Result.Success
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun syncAgenda(
        deletedEventIds: List<String>,
        deletedReminderIds: List<String>,
        deletedTaskIds: List<String>
    ): Result {

        return try {
            api.syncAgenda(SyncAgendaRequest(
                deletedEventIds = deletedEventIds,
                deletedReminderIds = deletedReminderIds,
                deletedTaskIds = deletedTaskIds
            ))
            syncCacheWithServer()
        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }

    }
}