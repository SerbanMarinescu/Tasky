package com.example.tasky.feature_agenda.data.repository

import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toTask
import com.example.tasky.feature_agenda.data.mapper.toTaskDto
import com.example.tasky.feature_agenda.data.mapper.toTaskEntity
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.TaskRepository
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import retrofit2.HttpException
import java.io.IOException

class TaskRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase
): TaskRepository {

    override suspend fun createTask(task: AgendaItem.Task): Resource<Unit> {

        val taskEntity = task.toTaskEntity()
        db.taskDao.upsertTask(taskEntity)

        return syncCreatedTask(task)
    }

    override suspend fun syncCreatedTask(task: AgendaItem.Task): Resource<Unit> {
        return try {
            api.createTask(task.toTaskDto())
            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun updateTask(task: AgendaItem.Task): Resource<Unit> {

        val taskEntity = task.toTaskEntity()
        db.taskDao.upsertTask(taskEntity)

        return syncUpdatedTask(task)
    }

    override suspend fun syncUpdatedTask(task: AgendaItem.Task): Resource<Unit> {
        return try {
            api.updateTask(task.toTaskDto())
            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun getTask(taskId: String): Resource<AgendaItem.Task> {

        val localTask = db.taskDao.getTaskById(taskId)

        localTask?.let {
            return Resource.Success(it.toTask())
        }

        return try {

            val response = api.getTask(taskId)
            val task = response.body()?.toTask() ?: return Resource.Error(message = "No such task was found!", errorType = ErrorType.OTHER)

            val taskEntity = task.toTaskEntity()
            db.taskDao.upsertTask(taskEntity)
            val newTask = db.taskDao.getTaskById(taskId) ?: return Resource.Error(message = "No such task was found!", errorType = ErrorType.OTHER)

            Resource.Success(newTask.toTask())
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun deleteTask(taskId: String): Resource<Unit> {
        db.taskDao.deleteTaskById(taskId)
        return syncDeletedTask(taskId)
    }

    override suspend fun syncDeletedTask(taskId: String): Resource<Unit> {
        return try {
            api.deleteTask(taskId)
            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }
}