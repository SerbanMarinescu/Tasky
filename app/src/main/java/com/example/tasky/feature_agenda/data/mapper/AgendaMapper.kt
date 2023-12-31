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
import com.example.tasky.feature_agenda.domain.model.EventPhoto
import com.example.tasky.feature_agenda.domain.util.ReminderType
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

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
        userId = userId,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt.toZonedDateTime()
    )
}

fun AttendeeDto.toAttendee(): Attendee {
    return Attendee(
        email = email,
        fullName = fullName,
        userId = userId,
        eventId = "",
        isGoing = true,
        remindAt = ZonedDateTime.now()
    )
}

fun PhotoDto.toPhoto(): EventPhoto.Remote {
    return EventPhoto.Remote(
        key = key,
        url = url
    )
}

fun EventDto.toEvent(): AgendaItem.Event {
    val timeDif = ChronoUnit.MINUTES.between(remindAt.toZonedDateTime(), from.toZonedDateTime())
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
        eventReminderType = when (timeDif) {
            10L -> ReminderType.TEN_MINUTES_BEFORE
            30L -> ReminderType.THIRTY_MINUTES_BEFORE
            60L -> ReminderType.ONE_HOUR_BEFORE
            6L * 60L -> ReminderType.SIX_HOURS_BEFORE
            else -> ReminderType.ONE_DAY_BEFORE
        }
    )
}


fun ReminderDto.toReminder(): AgendaItem.Reminder {
    val timeDif = ChronoUnit.MINUTES.between(remindAt.toZonedDateTime(), time.toZonedDateTime())
    return AgendaItem.Reminder(
        reminderId = id,
        reminderTitle = title,
        reminderDescription = description,
        time = time.toZonedDateTime(),
        remindAtTime = remindAt.toZonedDateTime(),
        typeOfReminder = when (timeDif) {
            10L -> ReminderType.TEN_MINUTES_BEFORE
            30L -> ReminderType.THIRTY_MINUTES_BEFORE
            60L -> ReminderType.ONE_HOUR_BEFORE
            6L * 60L -> ReminderType.SIX_HOURS_BEFORE
            else -> ReminderType.ONE_DAY_BEFORE
        }
    )
}


fun TaskDto.toTask(): AgendaItem.Task {
    val timeDif = ChronoUnit.MINUTES.between(remindAt.toZonedDateTime(), time.toZonedDateTime())
    return AgendaItem.Task(
        taskId = id,
        taskTitle = title,
        taskDescription = description,
        time = time.toZonedDateTime(),
        isDone = isDone,
        remindAtTime = remindAt.toZonedDateTime(),
        taskReminderType = when (timeDif) {
            10L -> ReminderType.TEN_MINUTES_BEFORE
            30L -> ReminderType.THIRTY_MINUTES_BEFORE
            60L -> ReminderType.ONE_HOUR_BEFORE
            6L * 60L -> ReminderType.SIX_HOURS_BEFORE
            else -> ReminderType.ONE_DAY_BEFORE
        }
    )
}

fun AttendeeEntity.toAttendee(): Attendee {
    return Attendee(
        email = email,
        fullName = fullName,
        userId = userId,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt.toZonedDateTime()
    )
}

fun PhotoEntity.toPhoto(): EventPhoto {
    return EventPhoto.Remote(
        key = key,
        url = url
    )
}

fun EventPhoto.toPhotoEntity(eventId: String): PhotoEntity? {
    return when(this) {
        is EventPhoto.Local -> {
            null
        }
        is EventPhoto.Remote -> {
            PhotoEntity(
                key = key,
                url = url,
                eventId = eventId
            )
        }
    }
}

fun EventWithAttendeesAndPhotos.toEvent(): AgendaItem.Event {
    return AgendaItem.Event(
        eventId = event.eventId,
        eventTitle = event.title,
        eventDescription = event.description,
        from = event.from.toZonedDateTime(),
        to = event.to.toZonedDateTime(),
        photos = photos.map { it.toPhoto() },
        attendees = attendees.map { it.toAttendee() },
        isUserEventCreator = event.isUserEventCreator,
        host = event.host,
        remindAtTime = event.remindAt.toZonedDateTime(),
        eventReminderType = event.reminderType
    )
}

fun TaskEntity.toTask(): AgendaItem.Task {
    return AgendaItem.Task(
        taskId = taskId,
        taskTitle = title,
        taskDescription = description,
        time = time.toZonedDateTime(),
        isDone = isDone,
        remindAtTime = remindAt.toZonedDateTime(),
        taskReminderType = reminderType
    )
}

fun ReminderEntity.toReminder(): AgendaItem.Reminder {
    return AgendaItem.Reminder(
        reminderId = reminderId,
        reminderTitle = title,
        reminderDescription = description,
        time = time.toZonedDateTime(),
        remindAtTime = remindAt.toZonedDateTime(),
        typeOfReminder = reminderType
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
        eventId = eventId,
        reminderType = eventReminderType
    )
}

fun AgendaItem.Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        title = taskTitle,
        description = taskDescription ?: "",
        time = time.toUtcTimestamp(),
        remindAt = remindAtTime.toUtcTimestamp(),
        isDone = isDone,
        taskId = taskId,
        reminderType = reminderType
    )
}

fun AgendaItem.Reminder.toReminderEntity(): ReminderEntity {
    return ReminderEntity(
        title = reminderTitle,
        description = reminderDescription ?: "",
        time = time.toUtcTimestamp(),
        remindAt = remindAtTime.toUtcTimestamp(),
        reminderId = reminderId,
        reminderType = typeOfReminder
    )
}

fun Attendee.toAttendeeEntity(eventId: String): AttendeeEntity {
    return AttendeeEntity(
        email = email,
        fullName = fullName,
        userId = userId,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt.toUtcTimestamp()
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


