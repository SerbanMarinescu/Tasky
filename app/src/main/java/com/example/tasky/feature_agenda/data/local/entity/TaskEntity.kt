package com.example.tasky.feature_agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasky.feature_agenda.domain.util.ReminderType

@Entity(tableName = "Task")
data class TaskEntity(
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean,
    val reminderType: ReminderType,
    @PrimaryKey
    val taskId: String = ""
)
