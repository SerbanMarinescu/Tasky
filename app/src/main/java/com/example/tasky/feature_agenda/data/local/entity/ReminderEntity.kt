package com.example.tasky.feature_agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Reminder")
data class ReminderEntity(
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    @PrimaryKey
    val reminderId: Int? = null
)
