package com.example.tasky.feature_agenda.data.local

import androidx.room.Dao
import androidx.room.Delete
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

    @Delete
    suspend fun deletePhotos(photos: List<PhotoEntity>)

    @Transaction
    @Query("SELECT * FROM Event WHERE eventId = :eventId")
    suspend fun getEventById(eventId: String): EventWithAttendeesAndPhotos?

    @Transaction
    @Query("SELECT * FROM Event WHERE `from` BETWEEN :startOfDay AND :endOfDay")
    fun getEventsForASpecificDay(startOfDay: Long, endOfDay: Long): Flow<List<EventWithAttendeesAndPhotos>>

    @Query("SELECT * FROM Attendee WHERE eventId = :eventId")
    suspend fun getAttendeesByEventId(eventId: String): List<AttendeeEntity>

    @Transaction
    @Query("DELETE FROM Event WHERE eventId = :eventId")
    suspend fun deleteEventById(eventId: String)

    @Query("DELETE FROM Attendee WHERE eventId = :eventId")
    suspend fun deleteAttendeeByEventId(eventId: String)

    @Delete
    suspend fun deleteAttendees(attendees: List<AttendeeEntity>)

    @Transaction
    suspend fun updateAttendeesAndPhotosForAnEvent(
        eventId: String,
        newAttendees: List<AttendeeEntity>?,
        newPhotos: List<PhotoEntity>?,
        deletedPhotos: List<PhotoEntity>
    ) {
        val currentAttendees = getAttendeesByEventId(eventId)
        deleteAttendees(currentAttendees)
        newAttendees?.let {
            upsertAttendees(newAttendees)
        }

        deletePhotos(deletedPhotos)
        newPhotos?.let {
            upsertPhotos(newPhotos)
        }
    }

    @Transaction
    @Query("DELETE FROM Event WHERE `from` BETWEEN :startOfDay AND :endOfDay")
    fun deleteEventsForASpecificDay(startOfDay: Long, endOfDay: Long)

    @Transaction
    @Query("DELETE FROM Event")
    suspend fun deleteEvents()
}