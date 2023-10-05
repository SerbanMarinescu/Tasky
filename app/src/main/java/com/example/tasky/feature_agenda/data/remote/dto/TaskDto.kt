package com.example.tasky.feature_agenda.data.remote.dto

data class TaskDto(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean
)
