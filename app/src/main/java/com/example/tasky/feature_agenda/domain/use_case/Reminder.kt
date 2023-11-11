package com.example.tasky.feature_agenda.domain.use_case

data class Reminder(
    val createReminder: CreateReminder,
    val updateReminder: UpdateReminder,
    val deleteReminder: DeleteReminder
)
