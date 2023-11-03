package com.example.tasky.feature_agenda.data.repository

import androidx.work.WorkManager
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toTask
import com.example.tasky.feature_agenda.data.mapper.toTaskDto
import com.example.tasky.feature_agenda.data.mapper.toTaskEntity
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.TaskRepository
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.io.IOException

class TaskRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase,
    private val workManager: WorkManager,
    private val moshi: Moshi
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

        val localTask = db.taskDao.getTask(taskId.toInt())

        localTask?.let {
            return Resource.Success(it.toTask())
        }

        return try {

            val response = api.getTask(taskId)
            val task = response.body()?.toTask() ?: return Resource.Error(message = "No such task was found!", errorType = ErrorType.OTHER)

            val taskEntity = task.toTaskEntity()
            db.taskDao.upsertTask(taskEntity)
            val newTask = db.taskDao.getTask(taskId.toInt()) ?: return Resource.Error(message = "No such task was found!", errorType = ErrorType.OTHER)

            Resource.Success(newTask.toTask())
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun deleteTask(task: AgendaItem.Task): Resource<Unit> {
        db.taskDao.deleteTask(task.taskId.toInt())
        return syncDeletedTask(task)
    }

    override suspend fun syncDeletedTask(task: AgendaItem.Task): Resource<Unit> {
        return try {
            api.deleteTask(task.taskId)
            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }
}