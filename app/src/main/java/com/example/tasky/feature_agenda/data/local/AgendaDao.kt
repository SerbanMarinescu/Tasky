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
import com.example.tasky.feature_agenda.data.mapper.toPhotoEntity
import com.example.tasky.feature_agenda.data.mapper.toReminderEntity
import com.example.tasky.feature_agenda.data.mapper.toTaskEntity
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AgendaDao {

    @Transaction
    suspend fun clearCache(db: AgendaDatabase) {
        db.apply {
            db.eventDao.deleteEvents()
            db.reminderDao.deleteReminders()
            db.taskDao.deleteTasks()
        }
    }

    @Transaction
    suspend fun updateFullAgenda(db: AgendaDatabase, agendaItems: List<AgendaItem>) {
        db.apply {

            clearCache(db)

            val eventList = agendaItems.filterIsInstance<AgendaItem.Event>()
            val remindersList = agendaItems.filterIsInstance<AgendaItem.Reminder>()
            val tasksList = agendaItems.filterIsInstance<AgendaItem.Task>()

            eventList.forEach { event ->
                val eventEntity = event.toEventEntity()
                val attendeeEntities = event.attendees.map { it.toAttendeeEntity(event.eventId) }
                val photoEntities = event.photos.mapNotNull { it.toPhotoEntity(event.eventId) }

                db.eventDao.upsertEvent(eventEntity)
                db.eventDao.upsertAttendees(attendeeEntities)
                db.eventDao.upsertPhotos(photoEntities)
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

            db.eventDao.deleteEventsForASpecificDay(startOfDay, endOfDay)
            db.reminderDao.deleteRemindersForSpecificDay(startOfDay, endOfDay)
            db.taskDao.deleteTasksForSpecificDay(startOfDay, endOfDay)

            val eventList = agendaItems.filterIsInstance<AgendaItem.Event>()
            val remindersList = agendaItems.filterIsInstance<AgendaItem.Reminder>()
            val tasksList = agendaItems.filterIsInstance<AgendaItem.Task>()

            eventList.forEach { event->
                val eventEntity = event.toEventEntity()
                val attendeeEntities = event.attendees.map { it.toAttendeeEntity(event.eventId) }
                val photoEntities = event.photos.mapNotNull { it.toPhotoEntity(event.eventId) }

                db.eventDao.upsertEvent(eventEntity)
                db.eventDao.upsertAttendees(attendeeEntities)
                db.eventDao.upsertPhotos(photoEntities)
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
    suspend fun upsertEventWithAttendees(
        db: AgendaDatabase,
        eventEntity: EventEntity,
        attendeeEntities: List<AttendeeEntity>,
        //photoEntities: List<PhotoEntity>
    ) {
        db.apply {

            db.eventDao.upsertEvent(eventEntity)
            db.eventDao.upsertAttendees(attendeeEntities)
            //db.eventDao.upsertPhotos(photoEntities)
        }
    }

    @Upsert
    suspend fun upsertItemToBeSynced(item: SyncItemEntity)

    @Delete
    suspend fun deleteSyncedItem(item: SyncItemEntity)

    @Query("SELECT * FROM SyncItems")
     fun getItemsToBeSynced(): Flow<List<SyncItemEntity>>
}