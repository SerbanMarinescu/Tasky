package com.example.tasky.feature_agenda.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.tasky.feature_agenda.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Upsert
    suspend fun upsertReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM Reminder WHERE reminderId = :reminderId")
    suspend fun getReminderById(reminderId: Int): ReminderEntity?

    @Query("SELECT * FROM Reminder WHERE time BETWEEN :startOfDay AND :endOfDay")
    fun getRemindersForSpecificDay(startOfDay: Long, endOfDay: Long): Flow<List<ReminderEntity>>

    @Query("DELETE FROM Reminder WHERE reminderId = :reminderId")
    suspend fun deleteReminderById(reminderId: Int)

    @Query("DELETE FROM Reminder WHERE time BETWEEN :startOfDay AND :endOfDay")
    suspend fun deleteRemindersForSpecificDay(startOfDay: Long, endOfDay: Long)

    @Query("DELETE FROM Reminder")
    suspend fun deleteReminders()
}