package com.example.tasky.feature_agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Event")
data class EventEntity(
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val isUserEventCreator: Boolean,
    val host: String,
    @PrimaryKey
    val eventId: String = ""
)
