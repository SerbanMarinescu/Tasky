package com.example.tasky.feature_agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasky.feature_agenda.domain.util.ReminderType

@Entity(tableName = "Reminder")
data class ReminderEntity(
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val reminderType: ReminderType,
    @PrimaryKey
    val reminderId: String = ""
)
