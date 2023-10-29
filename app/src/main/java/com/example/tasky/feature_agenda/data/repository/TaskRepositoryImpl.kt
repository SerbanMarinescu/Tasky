package com.example.tasky.feature_agenda.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tasky.common.Constants.ACTION
import com.example.tasky.common.Constants.TASK
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toTask
import com.example.tasky.feature_agenda.data.mapper.toTaskDto
import com.example.tasky.feature_agenda.data.mapper.toTaskEntity
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.data.util.ActionType
import com.example.tasky.feature_agenda.data.worker.TaskWorker
import com.example.tasky.feature_agenda.data.worker.enqueueWorker
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

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createTask(task: AgendaItem.Task) {

        val taskEntity = task.toTaskEntity()
        db.taskDao.upsertTask(taskEntity)

        val jsonTask = moshi.adapter(AgendaItem.Task::class.java).toJson(task)

        val inputData = Data.Builder()
            .putString(ACTION, ActionType.CREATE.toString())
            .putString(TASK, jsonTask)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<TaskWorker>()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateTask(task: AgendaItem.Task) {

        val taskEntity = task.toTaskEntity()
        db.taskDao.upsertTask(taskEntity)

        val jsonTask = moshi.adapter(AgendaItem.Task::class.java).toJson(task)

        val inputData = Data.Builder()
            .putString(ACTION, ActionType.UPDATE.toString())
            .putString(TASK, jsonTask)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<TaskWorker>()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    override suspend fun deleteTask(task: AgendaItem.Task) {

        db.taskDao.deleteTask(task.taskId.toInt())

        val jsonTask = moshi.adapter(AgendaItem.Task::class.java).toJson(task)

        val inputData = Data.Builder()
            .putString(ACTION, ActionType.DELETE.toString())
            .putString(TASK, jsonTask)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<TaskWorker>()
        )
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