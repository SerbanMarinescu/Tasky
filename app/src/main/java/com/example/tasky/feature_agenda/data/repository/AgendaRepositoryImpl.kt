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
import com.example.tasky.feature_agenda.data.remote.SyncAgendaRequest
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.AgendaRepository
import com.example.tasky.feature_agenda.domain.util.AgendaResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import retrofit2.HttpException
import java.io.IOException
import java.util.TimeZone

class AgendaRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase
) : AgendaRepository {

    override suspend fun logout() {
        api.logout()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAgenda(): Flow<List<AgendaItem>> {
        return combine(
            db.eventDao.getEvents(),
            db.reminderDao.getReminders(),
            db.taskDao.getTasks()) { events, reminders, tasks ->

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
    override suspend fun fetchAgendaFromRemote(): AgendaResult {

        val remoteAgenda = try {
            val response = api.getAgenda(
                timeZone = TimeZone.getDefault().toString(),
                time = System.currentTimeMillis()
            )

            val remoteEvents = response.body()?.events?.map { it.toEvent() } ?: emptyList()
            val remoteReminders = response.body()?.reminders?.map { it.toReminder() } ?: emptyList()
            val remoteTasks = response.body()?.tasks?.map { it.toTask() } ?: emptyList()

            remoteEvents + remoteTasks + remoteReminders

        } catch (e: HttpException) {
            return AgendaResult.Error(e.message ?: "Invalid Response")
        } catch (e: IOException) {
            return AgendaResult.Error(e.message ?: "Couldn't reach server")
        }

        remoteAgenda.let { agenda ->
            db.eventDao.deleteEvents()
            db.eventDao.deleteAttendees()
            db.reminderDao.deleteReminders()
            db.taskDao.deleteTasks()

            val eventList = agenda.filterIsInstance<AgendaItem.Event>()
            val remindersList = agenda.filterIsInstance<AgendaItem.Reminder>()
            val tasksList = agenda.filterIsInstance<AgendaItem.Task>()

            eventList.forEach { event->
                db.eventDao.upsertEvent(event.toEventEntity())
                db.eventDao.upsertAttendee(event.attendees.map { it.toAttendeeEntity(event.eventId) })
            }

            tasksList.forEach {
                db.taskDao.upsertTask(it.toTaskEntity())
            }

            remindersList.forEach {
                db.reminderDao.upsertReminder(it.toReminderEntity())
            }

            return AgendaResult.Success
        }
    }

    override suspend fun syncServerWithLocallyDeletedItems(
        eventIds: List<String>,
        reminderIds: List<String>,
        taskIds: List<String>
    ): AgendaResult {
        return try {
            api.syncAgenda(SyncAgendaRequest(
                deletedEventIds = eventIds,
                deletedReminderIds = reminderIds,
                deletedTaskIds = taskIds
            ))
            AgendaResult.Success
        } catch(e: HttpException) {
            AgendaResult.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            AgendaResult.Error(e.message ?: "Couldn't reach server")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun syncLocalCacheWithServer() {

        val remoteAgenda = try {
            val response = api.getFullAgenda()

            val remoteEvents = response.body()?.events?.map { it.toEvent() } ?: emptyList()
            val remoteReminders = response.body()?.reminders?.map { it.toReminder() } ?: emptyList()
            val remoteTasks = response.body()?.tasks?.map { it.toTask() } ?: emptyList()

            remoteEvents + remoteReminders + remoteTasks

        } catch(e: HttpException) {
            null
        } catch(e: IOException) {
            null
        }

        remoteAgenda?.let { agendaItem ->
            db.eventDao.deleteEvents()
            db.eventDao.deleteAttendees()
            db.reminderDao.deleteReminders()
            db.taskDao.deleteTasks()

            val eventList = agendaItem.filterIsInstance<AgendaItem.Event>()
            val remindersList = agendaItem.filterIsInstance<AgendaItem.Reminder>()
            val tasksList = agendaItem.filterIsInstance<AgendaItem.Task>()

            eventList.forEach { event->
                db.eventDao.upsertEvent(event.toEventEntity())
                db.eventDao.upsertAttendee(event.attendees.map { it.toAttendeeEntity(event.eventId) })
            }

            tasksList.forEach {
                db.taskDao.upsertTask(it.toTaskEntity())
            }

            remindersList.forEach {
                db.reminderDao.upsertReminder(it.toReminderEntity())
            }
        }

    }
}