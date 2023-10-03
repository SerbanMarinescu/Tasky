package com.example.tasky.feature_agenda.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.tasky.feature_agenda.data.local.entity.AttendeeEntity
import com.example.tasky.feature_agenda.data.local.entity.EventEntity

data class AttendeeWithEvent(
    @Embedded val attendee: AttendeeEntity,
    @Relation(
        parentColumn = "attendeeId",
        entityColumn = "eventId",
        associateBy = Junction(EventAttendeeCrossRef::class)
    )
    val events: List<EventEntity>
)