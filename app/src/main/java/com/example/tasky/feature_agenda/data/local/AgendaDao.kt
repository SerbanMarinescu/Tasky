package com.example.tasky.feature_agenda.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.tasky.feature_agenda.data.local.entity.AttendeeEntity
import com.example.tasky.feature_agenda.data.local.entity.EventEntity
import com.example.tasky.feature_agenda.data.local.entity.SyncItemEntity
import com.example.tasky.feature_agenda.data.mapper.toAttendeeEntity
import com.example.tasky.feature_agenda.data.mapper.toEventEntity
import com.example.tasky.feature_agenda.data.mapper.toReminderEntity
import com.example.tasky.feature_agenda.data.mapper.toTaskEntity
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AgendaDao {

    @Transaction
    suspend fun updateFullAgenda(db: AgendaDatabase, agendaItems: List<AgendaItem>) {
        db.apply {

            db.eventDao.deleteEvents()
            db.eventDao.deleteAttendees()
            db.reminderDao.deleteReminders()
            db.taskDao.deleteTasks()

            val eventList = agendaItems.filterIsInstance<AgendaItem.Event>()
            val remindersList = agendaItems.filterIsInstance<AgendaItem.Reminder>()
            val tasksList = agendaItems.filterIsInstance<AgendaItem.Task>()

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
        }
    }

    @Transaction
    suspend fun updateAgendaForSpecificDay(
        db: AgendaDatabase,
        agendaItems: List<AgendaItem>,
        startOfDay: Long,
        endOfDay: Long
    ) {
        db.apply {

            db.eventDao.deleteEventsForSpecificDay(startOfDay, endOfDay)
            db.eventDao.deleteAttendeesForSpecificDay(startOfDay, endOfDay)
            db.reminderDao.deleteRemindersForSpecificDay(startOfDay, endOfDay)
            db.taskDao.deleteTasksForSpecificDay(startOfDay, endOfDay)

            val eventList = agendaItems.filterIsInstance<AgendaItem.Event>()
            val remindersList = agendaItems.filterIsInstance<AgendaItem.Reminder>()
            val tasksList = agendaItems.filterIsInstance<AgendaItem.Task>()

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
        }
    }

    @Transaction
    suspend fun upsertEventWithAttendees(db: AgendaDatabase, eventEntity: EventEntity, attendeeEntities: List<AttendeeEntity>) {
        db.apply {

            db.eventDao.upsertEvent(eventEntity)
            db.eventDao.upsertAttendees(attendeeEntities)
        }
    }

    @Upsert
    suspend fun upsertItemToBeSynced(item: SyncItemEntity)

    @Delete
    suspend fun itemWasSynced(item: SyncItemEntity)

    @Query("SELECT * FROM SyncItems")
     fun getItemsToBeSynced(): Flow<List<SyncItemEntity>>
}