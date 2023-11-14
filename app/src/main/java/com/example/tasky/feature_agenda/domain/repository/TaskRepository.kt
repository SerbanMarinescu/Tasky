package com.example.tasky.feature_agenda.domain.repository

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.util.Resource

interface TaskRepository {

    suspend fun createTask(task: AgendaItem.Task): Resource<Unit>

    suspend fun syncCreatedTask(task: AgendaItem.Task): Resource<Unit>

    suspend fun updateTask(task: AgendaItem.Task): Resource<Unit>

    suspend fun syncUpdatedTask(task: AgendaItem.Task): Resource<Unit>

    suspend fun getTask(taskId: String): Resource<AgendaItem.Task>

    suspend fun deleteTask(taskId: String): Resource<Unit>

    suspend fun syncDeletedTask(taskId: String): Resource<Unit>
}