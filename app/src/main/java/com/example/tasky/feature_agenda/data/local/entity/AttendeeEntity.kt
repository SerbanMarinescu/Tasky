package com.example.tasky.feature_agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Attendee")
data class AttendeeEntity(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: Int,
    @PrimaryKey(autoGenerate = true)
    val attendeeId: Int = 0
)
