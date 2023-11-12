package com.example.tasky.feature_agenda.presentation.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatDateTimeOfPattern(zonedDateTime: ZonedDateTime, pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return zonedDateTime.format(formatter)
}