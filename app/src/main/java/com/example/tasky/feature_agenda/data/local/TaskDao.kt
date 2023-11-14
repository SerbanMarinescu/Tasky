package com.example.tasky.feature_agenda.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.tasky.feature_agenda.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    @Query("SELECT * FROM Task WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Query("SELECT * FROM Task WHERE time BETWEEN :startOfDay AND :endOfDay")
    fun getTasksForSpecificDay(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("DELETE FROM Task WHERE taskId = :taskId")
    suspend fun deleteTaskById(taskId: String)

    @Query("DELETE FROM Task WHERE time BETWEEN :startOfDay AND :endOfDay")
    suspend fun deleteTasksForSpecificDay(startOfDay: Long, endOfDay: Long)

    @Query("DELETE FROM Task")
    suspend fun deleteTasks()
}