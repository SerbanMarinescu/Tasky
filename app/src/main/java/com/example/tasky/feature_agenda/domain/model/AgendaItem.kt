package com.example.tasky.feature_agenda.domain.model

import com.example.tasky.feature_agenda.domain.util.ReminderType
import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime

@JsonClass(generateAdapter = true)
sealed class AgendaItem(
    val id: String,
    val title: String,
    val description: String?,
    val sortDate: ZonedDateTime,
    val remindAt: ZonedDateTime,
    val reminderType: ReminderType
) {
    @JsonClass(generateAdapter = true)
    data class Event(
        val eventId: String,
        val eventTitle: String,
        val eventDescription: String?,
        val from: ZonedDateTime,
        val to: ZonedDateTime,
        val photos: List<EventPhoto>,
        val attendees: List<Attendee>,
        val isUserEventCreator: Boolean,
        val host: String?,
        val remindAtTime: ZonedDateTime,
        val eventReminderType: ReminderType
    ) : AgendaItem(eventId, eventTitle, eventDescription, from, remindAtTime, eventReminderType)

    @JsonClass(generateAdapter = true)
    data class Task(
        val taskId: String,
        val taskTitle: String,
        val taskDescription: String?,
        val time: ZonedDateTime,
        val isDone: Boolean,
        val remindAtTime: ZonedDateTime,
        val taskReminderType: ReminderType
    ) : AgendaItem(taskId, taskTitle, taskDescription, time, remindAtTime, taskReminderType)

    @JsonClass(generateAdapter = true)
    data class Reminder(
        val reminderId: String,
        val reminderTitle: String,
        val reminderDescription: String?,
        val time: ZonedDateTime,
        val remindAtTime: ZonedDateTime,
        val typeOfReminder: ReminderType
    ) : AgendaItem(reminderId, reminderTitle, reminderDescription, time, remindAtTime, typeOfReminder)
}
