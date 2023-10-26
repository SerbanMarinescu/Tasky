package com.example.tasky.feature_agenda.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toAttendee
import com.example.tasky.feature_agenda.data.mapper.toAttendeeEntity
import com.example.tasky.feature_agenda.data.mapper.toEvent
import com.example.tasky.feature_agenda.data.mapper.toEventEntity
import com.example.tasky.feature_agenda.data.mapper.toUtcTimestamp
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.data.remote.request.EventRequest
import com.example.tasky.feature_agenda.data.remote.request.UpdateEventRequest
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.util.Result
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
    private val userPrefs: UserPreferences
): EventRepository {

    private suspend fun validateAttendees(attendees: List<Attendee>): Result<List<Attendee>> {

        val emailList = attendees.map { it.email }
        val validatedAttendees = mutableListOf<Attendee>()

        emailList.forEach { email ->

            try {

                val response = api.getAttendee(email)

                val userExists = response.body()?.doesUserExist ?: false
                val attendeeDto = response.body()?.attendee

                attendeeDto?.let {
                    val attendee = attendeeDto.toAttendee()

                    if(userExists) {
                        validatedAttendees.add(attendee)
                    }
                }

            } catch(e: HttpException) {
                return Result.Error(e.message ?: "Invalid Response")
            } catch(e: IOException) {
                return Result.Error(e.message ?: "Couldn't reach server")
            }
        }
        return Result.Success(validatedAttendees)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createEvent(
        event: AgendaItem.Event
    ): Result<Unit> {

       return try {

           val photoList = event.photos.map { photo ->
               val file = File(photo.url)
               val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
               MultipartBody.Part.createFormData("photos", file.name, requestFile)
           }

           var validatedAttendees = listOf<Attendee>()
           val doAttendeesExist = validateAttendees(event.attendees)

           when(doAttendeesExist) {
               is Result.Error -> {
                   return Result.Error(doAttendeesExist.message ?: "Attendees not found!")
               }
               is Result.Success -> {
                   validatedAttendees = doAttendeesExist.data ?: return Result.Error("Something went wrong!")
               }
           }

            api.createEvent(
                EventRequest(
                    id = event.eventId,
                    title = event.eventTitle,
                    description = event.eventDescription ?: "",
                    from = event.from.toUtcTimestamp(),
                    to = event.to.toUtcTimestamp(),
                    remindAt = event.remindAt.toUtcTimestamp(),
                    attendeeIds = validatedAttendees.map { it.userId }
                ),
                photoList
            )

           val eventEntity = event.toEventEntity()
           val attendeeEntities = validatedAttendees.map { it.toAttendeeEntity(event.eventId) }

           db.agendaDao.upsertEventWithAttendees(db, eventEntity, attendeeEntities)
           
           Result.Success()

        } catch(e: HttpException) {
           Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
           Result.Error(e.message ?: "Couldn't reach server")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getEvent(eventId: String): Result<AgendaItem.Event> {

        val localEvent = db.eventDao.getEventWithAttendees(eventId.toInt())

        localEvent?.let {
            return Result.Success(localEvent.toEvent())
        }

            return try {

                val response = api.getEvent(eventId)

                val event = response.body()?.toEvent() ?: return Result.Error("No such event found!")

                val eventEntity = event.toEventEntity()

                db.eventDao.upsertEvent(eventEntity)

                val newEvent = db.eventDao.getEventWithAttendees(eventId.toInt()) ?: return Result.Error("No such event found!")

                Result.Success(newEvent.toEvent())

            } catch(e: HttpException) {
                Result.Error(e.message ?: "Invalid Response")
            } catch(e: IOException) {
                Result.Error(e.message ?: "Couldn't reach server")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateEvent(event: AgendaItem.Event, deletedPhotos: List<Photo>): Result<Unit> {

        var validatedAttendees = listOf<Attendee>()
        val doAttendeesExist = validateAttendees(event.attendees)

        when(doAttendeesExist) {
            is Result.Error -> {
                return Result.Error(doAttendeesExist.message ?: "Attendees not found!")
            }
            is Result.Success -> {
                validatedAttendees = doAttendeesExist.data ?: return Result.Error("Something went wrong!")
            }
        }

        val eventEntity = event.toEventEntity()
        val attendeeEntities = validatedAttendees.map { it.toAttendeeEntity(event.eventId) }

        db.agendaDao.upsertEventWithAttendees(db, eventEntity, attendeeEntities)

        val photoList = event.photos.map { photo ->
            val file = File(photo.url)
            val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photos", file.name, requestFile)
        }

        val userId = userPrefs.getAuthenticatedUser()?.userId ?: return Result.Error("User is not logged in!")

        return try {

            api.updateEvent(
                UpdateEventRequest(
                    id = event.eventId,
                    title = event.title,
                    description = event.description ?: "",
                    from = event.from.toUtcTimestamp(),
                    to = event.to.toUtcTimestamp(),
                    remindAt = event.remindAt.toUtcTimestamp(),
                    attendeeIds = validatedAttendees.map { it.userId },
                    deletedPhotoKeys = deletedPhotos.map { it.key },
                    isGoing = validatedAttendees.any{ it.userId == userId }
                ),
                photoList
            )

            Result.Success()

        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }

    override suspend fun deleteEventAndAttendees(eventId: String): Result<Unit> {

        db.eventDao.deleteEventAndAttendees(eventId.toInt())

        return try {

            api.deleteEvent(eventId)
            api.deleteAttendee(eventId)

            Result.Success()

        } catch(e: HttpException) {
            Result.Error(e.message ?: "Invalid Response")
        } catch(e: IOException) {
            Result.Error(e.message ?: "Couldn't reach server")
        }
    }
}