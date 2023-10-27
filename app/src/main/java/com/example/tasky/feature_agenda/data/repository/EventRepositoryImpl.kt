package com.example.tasky.feature_agenda.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.mapper.toAttendeeEntity
import com.example.tasky.feature_agenda.data.mapper.toEvent
import com.example.tasky.feature_agenda.data.mapper.toEventEntity
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.data.worker.EventWorker
import com.example.tasky.feature_agenda.data.worker.enqueueWorker
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.util.Resource
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import retrofit2.HttpException
import java.io.IOException

class EventRepositoryImpl(
    private val api: TaskyAgendaApi,
    private val db: AgendaDatabase,
    private val workManager: WorkManager,
    private val moshi: Moshi
) : EventRepository {

    override suspend fun doesAttendeeExist(attendee: Attendee): Resource<Unit> {
        return try {
            val email = attendee.email
            val response = api.getAttendee(email)
            val doesUserExist =
                response.body()?.doesUserExist ?: return Resource.Error("User not found!")

            if (doesUserExist) {
                Resource.Success()
            } else {
                Resource.Error("User not found!")
            }
        } catch (e: HttpException) {
            return Resource.Error(e.message ?: "Invalid Response")
        } catch (e: IOException) {
            return Resource.Error(e.message ?: "Couldn't reach server")
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createEvent(
        event: AgendaItem.Event
    ): Resource<Unit> {

        val eventEntity = event.toEventEntity()
        val attendeeEntities = event.attendees.map { it.toAttendeeEntity(event.eventId) }

        db.agendaDao.upsertEventWithAttendees(db, eventEntity, attendeeEntities)

        val jsonEvent = moshi.adapter(AgendaItem.Event::class.java).toJson(event)
        val inputData = Data.Builder()
            .putString("action", "create")
            .putString("event", jsonEvent)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<EventWorker>()
        )

        return Resource.Success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getEvent(eventId: String): Resource<AgendaItem.Event> {

        val localEvent = db.eventDao.getEventWithAttendees(eventId.toInt())

        localEvent?.let {
            return Resource.Success(localEvent.toEvent())
        }

        val inputData = Data.Builder()
            .putString("action", "get")
            .putString("eventId", eventId)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<EventWorker>()
        )

        return Resource.Success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateEvent(
        event: AgendaItem.Event,
        deletedPhotos: List<Photo>
    ): Resource<Unit> {

        val eventEntity = event.toEventEntity()
        val attendeeEntities = event.attendees.map { it.toAttendeeEntity(event.eventId) }

        db.agendaDao.upsertEventWithAttendees(db, eventEntity, attendeeEntities)

        val jsonEvent = moshi.adapter(AgendaItem.Event::class.java).toJson(event)
        val jsonPhotoList = moshi.adapter<List<Photo>>(Types.newParameterizedType(List::class.java, Photo::class.java)).toJson(deletedPhotos)
        val inputData = Data.Builder()
            .putString("action", "update")
            .putString("event", jsonEvent)
            .putString("photoList", jsonPhotoList)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<EventWorker>()
        )

        return Resource.Success()
    }

    override suspend fun deleteEventAndAttendees(eventId: String): Resource<Unit> {

        db.eventDao.deleteEventAndAttendees(eventId.toInt())

        val inputData = Data.Builder()
            .putString("action", "delete")
            .putString("eventId", eventId)
            .build()

        enqueueWorker(
            workManager = workManager,
            inputData = inputData,
            requestBuilder = OneTimeWorkRequestBuilder<EventWorker>()
        )

        return Resource.Success()
    }
}