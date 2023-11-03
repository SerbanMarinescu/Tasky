package com.example.tasky.feature_agenda.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tasky.feature_agenda.data.local.entity.AttendeeEntity
import com.example.tasky.feature_agenda.data.local.entity.EventEntity
import com.example.tasky.feature_agenda.data.local.entity.PhotoEntity

data class EventWithAttendeesAndPhotos(
    @Embedded val event: EventEntity,

    @Relation(
        parentColumn = "eventId",
        entityColumn = "eventId"
    )
    val attendees: List<AttendeeEntity>,

    @Relation(
        parentColumn = "eventId",
        entityColumn = "eventId"
    )
    val photos: List<PhotoEntity>
)
