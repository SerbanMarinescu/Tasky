package com.example.tasky.feature_agenda.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tasky.feature_agenda.data.remote.dto.AttendeesDto
import com.example.tasky.feature_agenda.data.remote.dto.EventDto
import com.example.tasky.feature_agenda.data.remote.dto.PhotoDto
import com.example.tasky.feature_agenda.data.remote.dto.ReminderDto
import com.example.tasky.feature_agenda.data.remote.dto.TaskDto
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.Event
import com.example.tasky.feature_agenda.domain.model.Photo
import com.example.tasky.feature_agenda.domain.model.Reminder
import com.example.tasky.feature_agenda.domain.model.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
fun Long.toTime(): LocalDateTime {
    val instant = Instant.ofEpochMilli(this)
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
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
fun EventDto.toEvent() : Event {
    return Event(
        title = title,
        description = description,
        from = from.toTime(),
        to = to.toTime(),
        remindAt = remindAt.toTime(),
        attendees = attendees.map { it.toAttendee()},
        photos = photos.map { it.toPhoto() }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun ReminderDto.toReminder() : Reminder {
    return Reminder(
        title = title,
        description = description,
        time = time.toTime(),
        remindAt = remindAt.toTime()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun TaskDto.toTask() : Task {
    return Task(
        title = title,
        description = description,
        time = time.toTime(),
        remindAt = remindAt.toTime(),
        isDone = isDone
    )
}


