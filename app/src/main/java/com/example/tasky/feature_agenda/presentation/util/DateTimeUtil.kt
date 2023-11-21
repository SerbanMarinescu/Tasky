package com.example.tasky.feature_agenda.presentation.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatDateTimeOfPattern(zonedDateTime: ZonedDateTime, pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return zonedDateTime.format(formatter)
}

fun validateDates(fromDateTime: ZonedDateTime, toDateTime: ZonedDateTime): Boolean {
    return fromDateTime.isBefore(toDateTime)
}
enum class DateTimeDialogType {
    FROM_DATE,
    TO_DATE,
    FROM_TIME,
    TO_TIME
}