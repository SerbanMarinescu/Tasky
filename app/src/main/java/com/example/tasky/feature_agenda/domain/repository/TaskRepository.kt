package com.example.tasky.feature_agenda.domain.repository

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.util.Result

interface TaskRepository {

    suspend fun createTask(task: AgendaItem.Task): Result<Unit>

    suspend fun updateTask(task: AgendaItem.Task): Result<Unit>

    suspend fun getTask(taskId: String): Result<AgendaItem.Task>

    suspend fun deleteTask(taskId: String): Result<Unit>
}