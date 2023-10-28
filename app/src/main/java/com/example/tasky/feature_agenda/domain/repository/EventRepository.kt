package com.example.tasky.feature_agenda.domain.repository

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.util.Resource

interface EventRepository {

    suspend fun doesAttendeeExist(attendee: Attendee): Resource<Unit>

    suspend fun createEvent(event: AgendaItem.Event)

    suspend fun syncCreatedEvent(event: AgendaItem.Event): Resource<Unit>

    suspend fun getEvent(eventId: String): Resource<AgendaItem.Event>

    suspend fun updateEvent(event: AgendaItem.Event, deletedPhotos: List<Photo> = emptyList())

    suspend fun syncUpdatedEvent(event: AgendaItem.Event, deletedPhotos: List<Photo> = emptyList()): Resource<Unit>

    suspend fun deleteEvent(event: AgendaItem.Event)

    suspend fun syncDeletedEvent(event: AgendaItem.Event): Resource<Unit>
}