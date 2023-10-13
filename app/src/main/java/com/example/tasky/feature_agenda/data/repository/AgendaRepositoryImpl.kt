package com.example.tasky.feature_agenda.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tasky.feature_agenda.data.local.Daos
import com.example.tasky.feature_agenda.data.mapper.toAttendeeEntity
import com.example.tasky.feature_agenda.data.mapper.toEvent
import com.example.tasky.feature_agenda.data.mapper.toEventEntity
import com.example.tasky.feature_agenda.data.mapper.toReminder
import com.example.tasky.feature_agenda.data.mapper.toReminderEntity
import com.example.tasky.feature_agenda.data.mapper.toTask
import com.example.tasky.feature_agenda.data.mapper.toTaskEntity
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.AgendaRepository
import com.example.tasky.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.util.TimeZone

class AgendaRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val daos: Daos
) : AgendaRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAgenda(fetchFromRemote: Boolean): Flow<Resource<List<AgendaItem>>> {
        return flow {
            emit(Resource.Loading())

            val events = daos.eventDao.getEvents()

            val eventsWithAttendees = events.map {
                daos.eventDao.getEventWithAttendees(it.eventId)
            }

            val localEvents = eventsWithAttendees.map { it.toEvent() }
            val localTasks = daos.taskDao.getTasks().map { it.toTask() }
            val localReminders = daos.reminderDao.getReminders().map { it.toReminder() }

            val localAgenda = localEvents + localTasks + localReminders

            emit(Resource.Success(data = localAgenda))

            val isDbEmpty =
                localEvents.isEmpty() && localTasks.isEmpty() && localReminders.isEmpty()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteAgenda = try {
                val response = api.getAgenda(
                    timeZone = TimeZone.getDefault().toString(),
                    time = System.currentTimeMillis()
                )

                val remoteEvents = response.body()?.events?.map { it.toEvent() } ?: emptyList()
                val remoteTasks = response.body()?.tasks?.map { it.toTask() } ?: emptyList()
                val remoteReminders = response.body()?.reminders?.map { it.toReminder() } ?: emptyList()

                remoteEvents + remoteTasks + remoteReminders

            } catch (e: HttpException) {
                emit(Resource.Error(e.message ?: "Couldn't load data"))
                null
            } catch (e: IOException) {
                emit(Resource.Error(e.message ?: "Couldn't load data"))
                null
            }

            remoteAgenda?.let { agenda ->
                daos.eventDao.deleteEvents()
                daos.eventDao.deleteAttendees()
                daos.taskDao.deleteTasks()
                daos.reminderDao.deleteReminders()

                val eventList = agenda.filterIsInstance<AgendaItem.Event>()
                val tasksList = agenda.filterIsInstance<AgendaItem.Task>()
                val remindersList = agenda.filterIsInstance<AgendaItem.Reminder>()

                eventList.forEach { event->
                    daos.eventDao.upsertEvent(event.toEventEntity())
                    daos.eventDao.upsertAttendee(event.attendees.map { it.toAttendeeEntity(event.eventId) })
                }

                tasksList.forEach {
                    daos.taskDao.upsertTask(it.toTaskEntity())
                }

                remindersList.forEach {
                    daos.reminderDao.upsertReminder(it.toReminderEntity())
                }

                val insertedEventsList = daos.eventDao.getEvents()

                val insertedEventsWithAttendees = insertedEventsList.map {
                    daos.eventDao.getEventWithAttendees(it.eventId)
                }

                val insertedEvents = insertedEventsWithAttendees.map { it.toEvent() }
                val insertedTasks = daos.taskDao.getTasks().map { it.toTask() }
                val insertedReminders = daos.reminderDao.getReminders().map { it.toReminder() }

                val fullAgenda = insertedEvents + insertedTasks + insertedReminders

                emit(Resource.Success(data = fullAgenda))
                emit(Resource.Loading(false))
            }
        }
    }
}