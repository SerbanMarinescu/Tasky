package com.example.tasky.feature_agenda.data.local

data class Daos(
    val eventDao: EventDao,
    val reminderDao: ReminderDao,
    val taskDao: TaskDao
)
