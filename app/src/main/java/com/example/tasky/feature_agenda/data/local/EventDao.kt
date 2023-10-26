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

    @Transaction
    @Query("DELETE FROM Event WHERE eventId = :eventId")
    suspend fun deleteEventAndAttendees(eventId: Int)

    @Query("SELECT * FROM Event WHERE `from` >= :startOfDay AND `to` <= :endOfDay")
    fun getEventsForSpecificDay(startOfDay: Long, endOfDay: Long): Flow<List<EventEntity>>

    @Query("DELETE FROM Event WHERE `from` >= :startOfDay AND `to` <= :endOfDay")
    suspend fun deleteEventsForSpecificDay(startOfDay: Long, endOfDay: Long)

    @Upsert
    suspend fun upsertAttendees(attendees: List<AttendeeEntity>)

    @Query("DELETE FROM Attendee WHERE eventId IN (SELECT eventId FROM Event WHERE `from` >= :startOfDay AND `to` <= :endOfDay)")
    suspend fun deleteAttendeesForSpecificDay(startOfDay: Long, endOfDay: Long)

    @Transaction
    @Query("SELECT * FROM Event WHERE eventId = :eventId")
    suspend fun getEventWithAttendees(eventId: Int): EventWithAttendee?

    @Query("DELETE FROM Event")
    suspend fun deleteEvents()

    @Query("DELETE FROM Attendee")
    suspend fun deleteAttendees()
}