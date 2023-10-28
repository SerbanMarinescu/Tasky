package com.example.tasky.feature_agenda.data.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toEvent
import com.example.tasky.feature_agenda.data.mapper.toEventEntity
import com.example.tasky.feature_agenda.data.mapper.toUtcTimestamp
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.data.remote.request.EventRequest
import com.example.tasky.feature_agenda.data.remote.request.UpdateEventRequest
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.util.Resource
import com.example.tasky.util.Result
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class EventWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
    private val moshi: Moshi,
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase,
    private val userPrefs: UserPreferences
): CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            val action = workerParams.inputData.getString("action") ?: return@withContext Result.failure()
            val eventId = workerParams.inputData.getString("eventId")
            val jsonEvent = workerParams.inputData.getString("event") ?: return@withContext Result.failure()
            val jsonPhotoList = workerParams.inputData.getString("photoList")

            val eventAdapter = moshi.adapter(AgendaItem.Event::class.java)
            val photoAdapter = moshi.adapter<List<Photo>>(Types.newParameterizedType(List::class.java, Photo::class.java))

            val event = eventAdapter.fromJson(jsonEvent) ?: return@withContext Result.failure()
            val deletedPhotos = jsonPhotoList?.let {
                photoAdapter.fromJson(jsonPhotoList)
            } ?: emptyList()

             when(action) {
                "create" -> {
                    val result = createEvent(event)
                    getResult(result)
                }

                "get" -> {
                    val result = getEvent(eventId ?: return@withContext Result.failure())
                    getResult(result)
                }

                "update" -> {
                    val result = updateEvent(event, deletedPhotos)
                    getResult(result)
                }

                "delete" -> {
                    val result = deleteEventAndAttendees(eventId ?: return@withContext Result.failure())
                    getResult(result)
                }

                else -> Result.failure()
            }
        }
    }

    private fun <T> getResult(result: Resource<T>): Result {
        return when(result) {
            is Resource.Error -> {
                when(result.message) {
                    "Invalid Response" ->  Result.failure(
                        workDataOf("error" to result.message)
                    )
                    "Couldn't reach server" -> Result.retry()

                    else -> Result.failure(
                        workDataOf("error" to result.message)
                    )
                }
            }
            is Resource.Success -> {
                Result.success(
                    workDataOf("data" to result.data)
                )
            }
            else -> Result.failure()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun createEvent(event: AgendaItem.Event): Resource<Unit> {

        val photoList = event.photos.map { photo ->
            val file = File(photo.url)
            val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photos", file.name, requestFile)
        }

        return try {
            api.createEvent(
                EventRequest(
                    id = event.eventId,
                    title = event.eventTitle,
                    description = event.eventDescription ?: "",
                    from = event.from.toUtcTimestamp(),
                    to = event.to.toUtcTimestamp(),
                    remindAt = event.remindAt.toUtcTimestamp(),
                    attendeeIds = event.attendees.map { it.userId }
                ),
                photoList
            )
            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error("Invalid Response")
        } catch(e: IOException) {
            Resource.Error("Couldn't reach server")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getEvent(eventId: String): Resource<AgendaItem.Event> {

        return try {
            val response = api.getEvent(eventId)
            val event = response.body()?.toEvent() ?: return Resource.Error("No such event found!")

            val eventEntity = event.toEventEntity()
            db.eventDao.upsertEvent(eventEntity)
            val newEvent = db.eventDao.getEventWithAttendees(eventId.toInt()) ?: return Resource.Error("No such event found!")

            Resource.Success(newEvent.toEvent())
        } catch(e: HttpException) {
            Resource.Error("Invalid Response")
        } catch(e: IOException) {
            Resource.Error("Couldn't reach server")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateEvent(event: AgendaItem.Event, deletedPhotos: List<Photo> = emptyList()): Resource<Unit> {

        val photoList = event.photos.map { photo ->
            val file = File(photo.url)
            val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photos", file.name, requestFile)
        }

        val userId = userPrefs.getAuthenticatedUser()?.userId ?: return Resource.Error("User is not logged in!")

        return try {
            api.updateEvent(
                UpdateEventRequest(
                    id = event.eventId,
                    title = event.title,
                    description = event.description ?: "",
                    from = event.from.toUtcTimestamp(),
                    to = event.to.toUtcTimestamp(),
                    remindAt = event.remindAt.toUtcTimestamp(),
                    attendeeIds = event.attendees.map { it.userId },
                    deletedPhotoKeys = deletedPhotos.map { it.key },
                    isGoing = event.attendees.any{ it.userId == userId }
                ),
                photoList
            )

            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error("Invalid Response")
        } catch(e: IOException) {
            Resource.Error("Couldn't reach server")
        }
    }

    private suspend fun deleteEventAndAttendees(eventId: String): Resource<Unit> {
        return try {
            api.deleteEvent(eventId)
            api.deleteAttendee(eventId)
            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error("Invalid Response")
        } catch(e: IOException) {
            Resource.Error("Couldn't reach server")
        }
    }
}