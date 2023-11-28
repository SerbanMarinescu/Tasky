package com.example.tasky.feature_agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasky.feature_agenda.domain.util.ReminderType

@Entity(tableName = "Event")
data class EventEntity(
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val isUserEventCreator: Boolean,
    val host: String,
    val reminderType: ReminderType,
    @PrimaryKey
    val eventId: String = ""
)
