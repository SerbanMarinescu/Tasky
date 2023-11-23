package com.example.tasky.feature_agenda.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Attendee",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = arrayOf("eventId"),
            childColumns = arrayOf("eventId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class AttendeeEntity(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: Long,
    @PrimaryKey(autoGenerate = true)
    val attendeeId: Int = 0
)
