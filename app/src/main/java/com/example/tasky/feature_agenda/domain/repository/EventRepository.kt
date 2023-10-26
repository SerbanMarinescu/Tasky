package com.example.tasky.feature_agenda.domain.repository

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.util.Result

interface EventRepository {

    suspend fun createEvent(event: AgendaItem.Event): Result<Unit>

    suspend fun getEvent(eventId: String): Result<AgendaItem.Event>

    suspend fun updateEvent(event: AgendaItem.Event, deletedPhotos: List<Photo>): Result<Unit>

    suspend fun deleteEventAndAttendees(eventId: String): Result<Unit>
}