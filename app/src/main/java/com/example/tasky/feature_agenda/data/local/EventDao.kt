package com.example.tasky.feature_agenda.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.tasky.feature_agenda.data.local.entity.AttendeeEntity
import com.example.tasky.feature_agenda.data.local.entity.EventEntity
import com.example.tasky.feature_agenda.data.local.relations.EventWithAttendee
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Upsert
    suspend fun upsertEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM Event")
    suspend fun getEvents(): Flow<List<EventEntity>>

    @Upsert
    suspend fun upsertAttendee(attendees: List<AttendeeEntity>)

    @Delete
    suspend fun deleteAttendee(attendee: AttendeeEntity)

    @Transaction
    @Query("SELECT * FROM Event WHERE eventId = :eventId")
    suspend fun getEventWithAttendees(eventId: Int): EventWithAttendee

    @Query("DELETE FROM Event")
    suspend fun deleteEvents()

    @Query("DELETE FROM Attendee")
    suspend fun deleteAttendees()
}