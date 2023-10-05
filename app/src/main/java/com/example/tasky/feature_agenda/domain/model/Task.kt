package com.example.tasky.feature_agenda.domain.model

import java.time.LocalDateTime

data class Task(
    val title: String,
    val description: String,
    val time: LocalDateTime,
    val remindAt: LocalDateTime,
    val isDone: Boolean
)
