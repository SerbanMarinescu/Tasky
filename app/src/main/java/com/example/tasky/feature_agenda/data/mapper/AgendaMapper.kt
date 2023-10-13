package com.example.tasky.feature_agenda.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tasky.feature_agenda.data.local.entity.AttendeeEntity
import com.example.tasky.feature_agenda.data.local.entity.EventEntity
import com.example.tasky.feature_agenda.data.local.entity.ReminderEntity
import com.example.tasky.feature_agenda.data.local.entity.TaskEntity
import com.example.tasky.feature_agenda.data.local.relations.EventWithAttendee
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
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime


@RequiresApi(Build.VERSION_CODES.O)
fun Long.toZonedDateTime(timeZoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
    val instant = Instant.ofEpochMilli(this)
    return ZonedDateTime.ofInstant(instant, timeZoneId)
}

@RequiresApi(Build.VERSION_CODES.O)
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

fun PhotoDto.toPhoto(): Photo {
    return Photo(
        key = key,
        url = url
    )
}
@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
fun EventWithAttendee.toEvent(): AgendaItem.Event {
    return AgendaItem.Event(
        eventId = event.eventId.toString(),
        eventTitle = event.title,
        eventDescription = event.description,
        from = event.from.toZonedDateTime(),
        to = event.from.toZonedDateTime(),
        photos = emptyList(),
        attendees = attendees.map { it.toAttendee() },
        isUserEventCreator = event.isUserEventCreator,
        host = event.host,
        remindAtTime = event.remindAt.toZonedDateTime(),
        eventReminderType = ReminderType.ONE_HOUR_BEFORE
    )
}

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
fun AgendaItem.Event.toEventEntity(): EventEntity {
    return EventEntity(
        title = title,
        description = description ?: "",
        from = from.toUtcTimestamp(),
        to = to.toUtcTimestamp(),
        remindAt = remindAtTime.toUtcTimestamp(),
        isUserEventCreator = isUserEventCreator,
        host = host ?: "",
        eventId = id.toInt()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun AgendaItem.Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        title = title,
        description = description ?: "",
        time = time.toUtcTimestamp(),
        remindAt = remindAtTime.toUtcTimestamp(),
        isDone = isDone,
        taskId = id.toInt()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun AgendaItem.Reminder.toReminderEntity(): ReminderEntity {
    return ReminderEntity(
        title = title,
        description = description ?: "",
        time = time.toUtcTimestamp(),
        remindAt = remindAtTime.toUtcTimestamp(),
        reminderId = id.toInt()
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


