package com.example.tasky.feature_agenda.data.repository

import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toAttendee
import com.example.tasky.feature_agenda.data.mapper.toAttendeeEntity
import com.example.tasky.feature_agenda.data.mapper.toEvent
import com.example.tasky.feature_agenda.data.mapper.toEventEntity
import com.example.tasky.feature_agenda.data.mapper.toPhoto
import com.example.tasky.feature_agenda.data.mapper.toPhotoEntity
import com.example.tasky.feature_agenda.data.mapper.toUtcTimestamp
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.data.remote.request.EventRequest
import com.example.tasky.feature_agenda.data.remote.request.UpdateEventRequest
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.EventPhoto
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.feature_agenda.domain.util.JsonSerializer
import com.example.tasky.feature_agenda.domain.util.PhotoValidator
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class EventRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase,
    private val userPrefs: UserPreferences,
    private val photoValidator: PhotoValidator,
    private val jsonSerializer: JsonSerializer
) : EventRepository {

    override suspend fun doesAttendeeExist(email: String): Resource<Attendee> {
        return try {
            val response = api.getAttendee(email)
            val doesUserExist = response.body()?.doesUserExist ?: return Resource.Error(message = "User not found!", errorType = ErrorType.OTHER)
            val attendee = response.body()?.attendee?.toAttendee() ?: return Resource.Error(message = "User not found!", errorType = ErrorType.OTHER)

            if (doesUserExist) {
                Resource.Success(attendee)
            } else {
                Resource.Error(message = "User not found!", errorType = ErrorType.OTHER)
            }
        } catch (e: HttpException) {
            return Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch (e: IOException) {
            return Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun createEvent(event: AgendaItem.Event): Resource<Unit> {

        val eventEntity = event.toEventEntity()
        val attendeeEntities = event.attendees.map { it.toAttendeeEntity(event.eventId) }

        db.agendaDao.upsertEventWithAttendees(db, eventEntity, attendeeEntities)

        return syncCreatedEvent(event)
    }

    override suspend fun syncCreatedEvent(event: AgendaItem.Event): Resource<Unit> {

        val localPhotos = event.photos.filterIsInstance<EventPhoto.Local>()
        val eventDirectory = photoValidator.getDirectoryForImages(event.eventId)

        val photoList = List(localPhotos.size) { index ->
            val file = File(eventDirectory, "photo$index")
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo$index", file.name, requestFile)
        }

        val eventRequest = EventRequest(
            id = event.eventId,
            title = event.eventTitle,
            description = event.eventDescription ?: "",
            from = event.from.toUtcTimestamp(),
            to = event.to.toUtcTimestamp(),
            remindAt = event.remindAt.toUtcTimestamp(),
            attendeeIds = event.attendees.map { it.userId }
        )

        val jsonEventRequest = jsonSerializer.toJson(eventRequest, EventRequest::class.java)
        val eventRequestBody = jsonEventRequest.toRequestBody()

        return try {
           val response = api.createEvent(
               createEventRequest = eventRequestBody,
               photos = photoList

            )
            val remotePhotos = response.body()?.photos?.map {
                it.toPhoto()
            }
            val photoEntities = remotePhotos?.mapNotNull {
                it.toPhotoEntity(event.eventId)
            }
            photoEntities?.let {
                db.eventDao.upsertPhotos(it)
            }
            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun getEvent(eventId: String): Resource<AgendaItem.Event> {

        val localEvent = db.eventDao.getEventById(eventId)

        localEvent?.let {
            return Resource.Success(localEvent.toEvent())
        }

        return try {
            val response = api.getEvent(eventId)
            val event = response.body()?.toEvent() ?: return Resource.Error(message = "No such event found!", errorType = ErrorType.OTHER)

            val eventEntity = event.toEventEntity()
            db.eventDao.upsertEvent(eventEntity)
            val newEvent = db.eventDao.getEventById(eventId) ?: return Resource.Error(message = "No such event found!", errorType = ErrorType.OTHER)

            Resource.Success(newEvent.toEvent())
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun updateEvent(event: AgendaItem.Event, deletedPhotos: List<EventPhoto>): Resource<Unit> {

        val eventEntity = event.toEventEntity()
        val attendeeEntities = event.attendees.map { it.toAttendeeEntity(event.eventId) }
        //val photoEntities = event.photos.map { it.toPhotoEntity(event.eventId) }

        db.agendaDao.upsertEventWithAttendees(db, eventEntity, attendeeEntities)

        return syncUpdatedEvent(event, deletedPhotos)
    }

    override suspend fun syncUpdatedEvent(
        event: AgendaItem.Event,
        deletedPhotos: List<EventPhoto>
    ): Resource<Unit> {

        val photoList = event.photos.map { photo ->
            val file = File(
                when(photo) {
                    is EventPhoto.Local -> photo.uri
                    is EventPhoto.Remote -> photo.url
                }
            )
            val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photos", file.name, requestFile)
        }

        val userId = userPrefs.getAuthenticatedUser()?.userId ?: return Resource.Error(message = "User is not logged in!", errorType = ErrorType.OTHER)

        return try {
            val response = api.updateEvent(
                UpdateEventRequest(
                    id = event.eventId,
                    title = event.title,
                    description = event.description ?: "",
                    from = event.from.toUtcTimestamp(),
                    to = event.to.toUtcTimestamp(),
                    remindAt = event.remindAt.toUtcTimestamp(),
                    attendeeIds = event.attendees.map { it.userId },
                    deletedPhotoKeys = deletedPhotos.map {
                        when (it) {
                            is EventPhoto.Local -> it.key
                            is EventPhoto.Remote -> it.key
                        }
                    },
                    isGoing = event.attendees.any{ it.userId == userId }
                ),
                photoList
            )

//            val remotePhotos = response.body()?.photos?.map {
//                it.toPhoto()
//            }
//            val photoEntities = remotePhotos?.map {
//                it.toPhotoEntity(event.eventId)
//            }
//            photoEntities?.let {
//                db.eventDao.upsertPhotos(it)
//            }

            Resource.Success()
        } catch(e: HttpException) {
            Resource.Error(message = e.message ?: "Invalid Response", errorType = ErrorType.HTTP)
        } catch(e: IOException) {
            Resource.Error(message = e.message ?: "Couldn't reach server", errorType = ErrorType.IO)
        }
    }

    override suspend fun deleteEvent(event: AgendaItem.Event): Resource<Unit> {

        if(event.isUserEventCreator) {
            db.eventDao.deleteEventById(event.eventId)
        } else {
            db.eventDao.deleteAttendeeByEventId(event.eventId)
        }

        return syncDeletedEvent(event)
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