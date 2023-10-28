package com.example.tasky.feature_agenda.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toTask
import com.example.tasky.feature_agenda.data.mapper.toTaskDto
import com.example.tasky.feature_agenda.data.mapper.toTaskEntity
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.TaskRepository
import com.example.tasky.util.Result
import retrofit2.HttpException
import java.io.IOException

class TaskRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase
): TaskRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createTask(task: AgendaItem.Task): Result<Unit> {

        val taskEntity = task.toTaskEntity()
        db.taskDao.upsertTask(taskEntity)

        return try {
            api.createTask(task.toTaskDto())
            Result.Success()
        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateTask(task: AgendaItem.Task): Result<Unit> {

        val taskEntity = task.toTaskEntity()
        db.taskDao.upsertTask(taskEntity)

        return try {
            api.updateTask(task.toTaskDto())
            Result.Success()
        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getTask(taskId: String): Result<AgendaItem.Task> {

        val localTask = db.taskDao.getTask(taskId.toInt())

        localTask?.let {
            return Result.Success(it.toTask())
        }

        return try {

            val response = api.getTask(taskId)
            val task = response.body()?.toTask() ?: return Result.Error("No such task was found!")

            val taskEntity = task.toTaskEntity()
            db.taskDao.upsertTask(taskEntity)
            val newTask = db.taskDao.getTask(taskId.toInt()) ?: return Result.Error("No such task was found!")

            Result.Success(newTask.toTask())
        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {

        db.taskDao.deleteTask(taskId.toInt())

        return try {
            api.deleteTask(taskId)
            Result.Success()
        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }
}