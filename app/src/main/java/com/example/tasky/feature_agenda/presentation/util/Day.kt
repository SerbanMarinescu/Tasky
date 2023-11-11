package com.example.tasky.feature_agenda.presentation.util

import java.time.DayOfWeek
import java.time.ZonedDateTime

data class Day(
    val dayOfWeek: DayOfWeek,
    val dayOfMonth: Int
)

fun generateNextDays(currentDate: ZonedDateTime): List<Day> {
    return (0..5).map {
        val day = currentDate.plusDays(it.toLong())
        Day(
            dayOfWeek = day.dayOfWeek,
            dayOfMonth = day.dayOfMonth
        )
    }
}
