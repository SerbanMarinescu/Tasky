package com.example.tasky.feature_agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "Task")
data class TaskEntity(
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean,
    @PrimaryKey
    val taskId: Int? = null
)
