package com.example.tasky.feature_agenda.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tasky.common.Constants.ACTION
import com.example.tasky.common.Constants.DELETED_PHOTOS
import com.example.tasky.common.Constants.EVENT
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toAttendeeEntity
import com.example.tasky.feature_agenda.data.mapper.toEvent
import com.example.tasky.feature_agenda.data.mapper.toEventEntity
import com.example.tasky.feature_agenda.data.mapper.toUtcTimestamp
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.data.remote.request.EventRequest
import com.example.tasky.feature_agenda.data.remote.request.UpdateEventRequest
import com.example.tasky.feature_agenda.data.util.ActionType
import com.example.tasky.feature_agenda.data.worker.EventWorker
import com.example.tasky.feature_agenda.data.worker.enqueueWorker
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class EventRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase,
    private val workManager: WorkManager,
    private val userPrefs: UserPreferences,
    private val moshi: Moshi
) : EventRepository {

    override suspend fun doesAttendeeExist(attendee: Attendee): Resource<Unit> {
        return try {
            val email = attendee.email
            val response = api.getAttendee(email)
            val doesUserExist = response.body()?.doesUserExist ?: return Resource.Error(message = "User not found!", errorType = ErrorType.OTHER)

            if (doesUserExist) {
                Resource.Success()
            } else {
                Resource.Error(message = "User not found!", errorType = ErrorType.OTHER)
            }
        } catch (e: HttpException) {
            return Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch (e: IOException) {
            return Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createEvent(event: AgendaItem.Event) {

        val eventEntity = event.toEventEntity()
        val attendeeEntities = event.attendees.map { it.toAttendeeEntity(event.eventId) }

        db.agendaDao.upsertEventWithAttendees(db, eventEntity, attendeeEntities)

        val jsonEvent = moshi.adapter(AgendaItem.Event::class.java).toJson(event)
        val inputData = Data.Builder()
            .putString(ACTION, ActionType.CREATE.toString())
            .putString(EVENT, jsonEvent)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<EventWorker>()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun syncCreatedEvent(event: AgendaItem.Event): Resource<Unit> {

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
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getEvent(eventId: String): Resource<AgendaItem.Event> {

        val localEvent = db.eventDao.getEventWithAttendees(eventId.toInt())

        localEvent?.let {
            return Resource.Success(localEvent.toEvent())
        }

        return try {
            val response = api.getEvent(eventId)
            val event = response.body()?.toEvent() ?: return Resource.Error(message = "No such event found!", errorType = ErrorType.OTHER)

            val eventEntity = event.toEventEntity()
            db.eventDao.upsertEvent(eventEntity)
            val newEvent = db.eventDao.getEventWithAttendees(eventId.toInt()) ?: return Resource.Error(message = "No such event found!", errorType = ErrorType.OTHER)

            Resource.Success(newEvent.toEvent())
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateEvent(event: AgendaItem.Event, deletedPhotos: List<Photo>) {

        val eventEntity = event.toEventEntity()
        val attendeeEntities = event.attendees.map { it.toAttendeeEntity(event.eventId) }

        db.agendaDao.upsertEventWithAttendees(db, eventEntity, attendeeEntities)

        val jsonEvent = moshi.adapter(AgendaItem.Event::class.java).toJson(event)
        val jsonPhotoList = moshi.adapter<List<Photo>>(Types.newParameterizedType(List::class.java, Photo::class.java)).toJson(deletedPhotos)
        val inputData = Data.Builder()
            .putString(ACTION, ActionType.UPDATE.toString())
            .putString(EVENT, jsonEvent)
            .putString(DELETED_PHOTOS, jsonPhotoList)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<EventWorker>()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun syncUpdatedEvent(
        event: AgendaItem.Event,
        deletedPhotos: List<Photo>
    ): Resource<Unit> {

        val photoList = event.photos.map { photo ->
            val file = File(photo.url)
            val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photos", file.name, requestFile)
        }

        val userId = userPrefs.getAuthenticatedUser()?.userId ?: return Resource.Error(message = "User is not logged in!", errorType = ErrorType.OTHER)

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
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun deleteEvent(event: AgendaItem.Event) {

        if(event.isUserEventCreator) {
            db.eventDao.deleteEventAndAttendees(event.eventId.toInt())
        } else {
            db.eventDao.deleteAttendee(event.eventId.toInt())
        }

        val jsonEvent = moshi.adapter(AgendaItem.Event::class.java).toJson(event)
        val inputData = Data.Builder()
            .putString(ACTION, ActionType.DELETE.toString())
            .putString(EVENT, jsonEvent)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<EventWorker>()
        )
    }

    override suspend fun syncDeletedEvent(event: AgendaItem.Event): Resource<Unit> {
        return try {
            if(event.isUserEventCreator) {
                api.deleteEvent(event.eventId)
            } else {
                api.deleteAttendee(event.eventId)
            }
            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }
}