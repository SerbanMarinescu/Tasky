package com.example.tasky.feature_agenda.domain.repository

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.EventPhoto
import com.example.tasky.util.Resource

interface EventRepository {

    suspend fun doesAttendeeExist(email: String): Resource<Attendee>

    suspend fun createEvent(event: AgendaItem.Event): Resource<Unit>

    suspend fun syncCreatedEvent(event: AgendaItem.Event): Resource<Unit>

    suspend fun getEvent(eventId: String): Resource<AgendaItem.Event>

    suspend fun updateEvent(event: AgendaItem.Event, deletedPhotos: List<EventPhoto> = emptyList()): Resource<Unit>

    suspend fun syncUpdatedEvent(event: AgendaItem.Event, deletedPhotos: List<EventPhoto> = emptyList()): Resource<Unit>

    suspend fun deleteEvent(eventId: String, isUserEventCreator: Boolean): Resource<Unit>

    suspend fun syncDeletedEvent(eventId: String, isUserEventCreator: Boolean): Resource<Unit>
}