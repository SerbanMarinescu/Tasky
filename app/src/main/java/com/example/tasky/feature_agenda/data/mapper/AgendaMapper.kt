package com.example.tasky.feature_agenda.data.mapper

import com.example.tasky.feature_agenda.data.local.entity.AttendeeEntity
import com.example.tasky.feature_agenda.data.local.entity.EventEntity
import com.example.tasky.feature_agenda.data.local.entity.PhotoEntity
import com.example.tasky.feature_agenda.data.local.entity.ReminderEntity
import com.example.tasky.feature_agenda.data.local.entity.TaskEntity
import com.example.tasky.feature_agenda.data.local.relations.EventWithAttendeesAndPhotos
import com.example.tasky.feature_agenda.data.remote.dto.AttendeeDto
import com.example.tasky.feature_agenda.data.remote.dto.AttendeesDto
import com.example.tasky.feature_agenda.data.remote.dto.EventDto
import com.example.tasky.feature_agenda.data.remote.dto.PhotoDto
import com.example.tasky.feature_agenda.data.remote.dto.ReminderDto
import com.example.tasky.feature_agenda.data.remote.dto.TaskDto
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.util.ReminderType
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun Long.toZonedDateTime(timeZoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
    val instant = Instant.ofEpochMilli(this)
    return ZonedDateTime.ofInstant(instant, timeZoneId)
}

fun ZonedDateTime.toUtcTimestamp(): Long {
    val instant = this.toInstant()
    return instant.toEpochMilli()
}

fun AttendeesDto.toAttendee(): Attendee {
    return Attendee(
        email = email,
        fullName = fullName,
        userId = userId
    )
}

fun AttendeeDto.toAttendee(): Attendee {
    return Attendee(
        email = email,
        fullName = fullName,
        userId = userId
    )
}

fun PhotoDto.toPhoto(): Photo {
    return Photo(
        key = key,
        url = url
    )
}

fun EventDto.toEvent(): AgendaItem.Event {
    return AgendaItem.Event(
        eventId = id,
        eventTitle = title,
        eventDescription = description,
        from = from.toZonedDateTime(),
        to = to.toZonedDateTime(),
        photos = photos.map { it.toPhoto() },
        attendees = attendees.map { it .toAttendee()},
        isUserEventCreator = isUserEventCreator,
        host = host,
        remindAtTime = remindAt.toZonedDateTime(),
        eventReminderType = ReminderType.ONE_HOUR_BEFORE
    )
}


fun ReminderDto.toReminder(): AgendaItem.Reminder {
    return AgendaItem.Reminder(
        reminderId = id,
        reminderTitle = title,
        reminderDescription = description,
        time = time.toZonedDateTime(),
        remindAtTime = remindAt.toZonedDateTime(),
        typeOfReminder = ReminderType.ONE_HOUR_BEFORE
    )
}


fun TaskDto.toTask(): AgendaItem.Task {
    return AgendaItem.Task(
        taskId = id,
        taskTitle = title,
        taskDescription = description,
        time = time.toZonedDateTime(),
        isDone = isDone,
        remindAtTime = remindAt.toZonedDateTime(),
        taskReminderType = ReminderType.ONE_HOUR_BEFORE
    )
}

fun AttendeeEntity.toAttendee(): Attendee {
    return Attendee(
        email = email,
        fullName = fullName,
        userId = userId
    )
}

fun PhotoEntity.toPhoto(): Photo {
    return Photo(
        key = key,
        url = url
    )
}

fun Photo.toPhotoEntity(eventId: String): PhotoEntity {
    return PhotoEntity(
        key = key,
        url = url,
        eventId = eventId
    )
}
fun EventWithAttendeesAndPhotos.toEvent(): AgendaItem.Event {
    return AgendaItem.Event(
        eventId = event.eventId.toString(),
        eventTitle = event.title,
        eventDescription = event.description,
        from = event.from.toZonedDateTime(),
        to = event.from.toZonedDateTime(),
        photos = photos.map { it.toPhoto() },
        attendees = attendees.map { it.toAttendee() },
        isUserEventCreator = event.isUserEventCreator,
        host = event.host,
        remindAtTime = event.remindAt.toZonedDateTime(),
        eventReminderType = ReminderType.ONE_HOUR_BEFORE
    )
}

fun TaskEntity.toTask(): AgendaItem.Task {
    return AgendaItem.Task(
        taskId = taskId.toString(),
        taskTitle = title,
        taskDescription = description,
        time = time.toZonedDateTime(),
        isDone = isDone,
        remindAtTime = remindAt.toZonedDateTime(),
        taskReminderType = ReminderType.ONE_HOUR_BEFORE
    )
}

fun ReminderEntity.toReminder(): AgendaItem.Reminder {
    return AgendaItem.Reminder(
        reminderId = reminderId.toString(),
        reminderTitle = title,
        reminderDescription = description,
        time = time.toZonedDateTime(),
        remindAtTime = remindAt.toZonedDateTime(),
        typeOfReminder = ReminderType.ONE_HOUR_BEFORE
    )
}

fun AgendaItem.Event.toEventEntity(): EventEntity {
    return EventEntity(
        title = eventTitle,
        description = eventDescription ?: "",
        from = from.toUtcTimestamp(),
        to = to.toUtcTimestamp(),
        remindAt = remindAtTime.toUtcTimestamp(),
        isUserEventCreator = isUserEventCreator,
        host = host ?: "",
        eventId = eventId.toInt()
    )
}

fun AgendaItem.Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        title = taskTitle,
        description = taskDescription ?: "",
        time = time.toUtcTimestamp(),
        remindAt = remindAtTime.toUtcTimestamp(),
        isDone = isDone,
        taskId = taskId.toInt()
    )
}

fun AgendaItem.Reminder.toReminderEntity(): ReminderEntity {
    return ReminderEntity(
        title = reminderTitle,
        description = reminderDescription ?: "",
        time = time.toUtcTimestamp(),
        remindAt = remindAtTime.toUtcTimestamp(),
        reminderId = reminderId.toInt()
    )
}

fun Attendee.toAttendeeEntity(eventId: String): AttendeeEntity {
    return AttendeeEntity(
        email = email,
        fullName = fullName,
        userId = userId,
        eventId = eventId.toInt()
    )
}

fun AgendaItem.Task.toTaskDto(): TaskDto {
    return TaskDto(
        id = taskId,
        title = taskTitle,
        description = taskDescription ?: "",
        time = time.toUtcTimestamp(),
        remindAt = remindAtTime.toUtcTimestamp(),
        isDone = isDone
    )
}

fun AgendaItem.Reminder.toReminderDto(): ReminderDto {
    return ReminderDto(
        id = reminderId,
        title = reminderTitle,
        description = reminderDescription ?: "",
        time = time.toUtcTimestamp(),
        remindAt = remindAtTime.toUtcTimestamp()
    )
}


