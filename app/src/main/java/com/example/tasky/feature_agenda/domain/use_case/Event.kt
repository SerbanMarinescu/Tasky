package com.example.tasky.feature_agenda.domain.use_case

data class Event(
    val createEvent: CreateEvent,
    val updateEvent: UpdateEvent,
    val deleteEvent: DeleteEvent,
    val validateAttendee: ValidateAttendee,
    val validatePhotos: ValidatePhotos
)
