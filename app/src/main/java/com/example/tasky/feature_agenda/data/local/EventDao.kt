package com.example.tasky.feature_agenda.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.tasky.feature_agenda.data.local.entity.AttendeeEntity
import com.example.tasky.feature_agenda.data.local.entity.EventEntity
import com.example.tasky.feature_agenda.data.local.entity.PhotoEntity
import com.example.tasky.feature_agenda.data.local.relations.EventWithAttendeesAndPhotos
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Upsert
    suspend fun upsertEvent(event: EventEntity)

    @Upsert
    suspend fun upsertAttendees(attendees: List<AttendeeEntity>)

    @Upsert
    suspend fun upsertPhotos(photos: List<PhotoEntity>)

    @Transaction
    @Query("SELECT * FROM Event WHERE eventId = :eventId")
    suspend fun getEventById(eventId: Int): EventWithAttendeesAndPhotos?

    @Transaction
    @Query("SELECT * FROM Event WHERE `from` >= :startOfDay AND `to` <= :endOfDay")
    fun getEventsForASpecificDay(startOfDay: Long, endOfDay: Long): Flow<List<EventWithAttendeesAndPhotos>>

    @Transaction
    @Query("DELETE FROM Event WHERE eventId = :eventId")
    suspend fun deleteEventById(eventId: Int)

    @Query("DELETE FROM Attendee WHERE eventId = :eventId")
    suspend fun deleteAttendeeByEventId(eventId: Int)

    @Transaction
    @Query("DELETE FROM Event WHERE `from` >= :startOfDay AND `to` <= :endOfDay")
    fun deleteEventsForASpecificDay(startOfDay: Long, endOfDay: Long)

    @Transaction
    @Query("DELETE FROM Event")
    suspend fun deleteEvents()
}