package com.example.tasky.feature_agenda.domain.model

data class Reminder(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long
)
