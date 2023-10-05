package com.example.tasky.feature_agenda.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.tasky.feature_agenda.data.local.entity.AttendeeEntity
import com.example.tasky.feature_agenda.data.local.entity.EventEntity

data class EventWithAttendee(
    @Embedded val event: EventEntity,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "attendeeId",
        associateBy = Junction(EventAttendeeCrossRef::class)
    )
    val attendees: List<AttendeeEntity>
)
