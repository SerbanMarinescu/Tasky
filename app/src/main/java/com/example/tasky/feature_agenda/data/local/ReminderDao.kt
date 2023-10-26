package com.example.tasky.feature_agenda.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.tasky.feature_agenda.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Upsert
    suspend fun upsertReminder(reminder: ReminderEntity)

    @Query("DELETE FROM Reminder WHERE reminderId = :reminderId")
    suspend fun deleteReminder(reminderId: Int)

    @Query("SELECT * FROM Reminder WHERE reminderId = :reminderId")
    suspend fun getReminder(reminderId: Int): ReminderEntity?

    @Query("SELECT * FROM Reminder WHERE time BETWEEN :startOfDay AND :endOfDay")
    fun getRemindersForSpecificDay(startOfDay: Long, endOfDay: Long): Flow<List<ReminderEntity>>

    @Query("DELETE FROM Reminder WHERE time BETWEEN :startOfDay AND :endOfDay")
    fun deleteRemindersForSpecificDay(startOfDay: Long, endOfDay: Long)

    @Query("DELETE FROM Reminder")
    suspend fun deleteReminders()
}