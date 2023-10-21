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

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM Reminder")
    suspend fun getReminders(): Flow<List<ReminderEntity>>

    @Query("DELETE FROM Reminder")
    suspend fun deleteReminders()
}