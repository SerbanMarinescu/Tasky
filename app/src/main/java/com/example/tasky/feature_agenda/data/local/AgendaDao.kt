package com.example.tasky.feature_agenda.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.tasky.feature_agenda.data.local.entity.AttendeeEntity
import com.example.tasky.feature_agenda.data.local.entity.EventEntity
import com.example.tasky.feature_agenda.data.local.entity.ReminderEntity
import com.example.tasky.feature_agenda.data.local.entity.TaskEntity
import com.example.tasky.feature_agenda.data.local.relations.EventWithAttendee

@Dao
interface AgendaDao {

    @Upsert
    suspend fun upsertEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM Event")
    suspend fun getEvents(): List<EventEntity>

    @Upsert
    suspend fun upsertAttendee(attendee: AttendeeEntity)

    @Delete
    suspend fun deleteAttendee(attendee: AttendeeEntity)

    @Transaction
    @Query("SELECT * FROM Event WHERE eventId = :eventId")
    suspend fun getEventWithAttendees(eventId: Int): List<EventWithAttendee>

    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM Task")
    suspend fun getTasks(): List<TaskEntity>

    @Upsert
    suspend fun upsertReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM Reminder")
    suspend fun getReminders(): List<ReminderEntity>
}