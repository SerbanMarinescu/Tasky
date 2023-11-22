package com.example.tasky.feature_agenda.presentation.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatDateTimeOfPattern(zonedDateTime: ZonedDateTime, pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return zonedDateTime.format(formatter)
}

fun validateDateRange(fromDateTime: ZonedDateTime, toDateTime: ZonedDateTime): Boolean {
    return fromDateTime.isBefore(toDateTime)
}

fun validateDateRange(fromDate: LocalDate, toDate: LocalDate): Boolean {
    return fromDate.isBefore(toDate)
}

fun validateDateRange(fromTime: LocalTime, toTime: LocalTime): Boolean {
    return fromTime.isBefore(toTime)
}

enum class DateTimeDialogType {
    FROM_DATE,
    TO_DATE,
    FROM_TIME,
    TO_TIME
}