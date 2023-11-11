package com.example.tasky.feature_agenda.presentation.util

import java.time.DayOfWeek
import java.time.ZonedDateTime

data class Day(
    val dayOfWeek: DayOfWeek,
    val dayOfMonth: Int
)

fun generateNextDays(currentDate: ZonedDateTime): List<Day> {

    val nextDays = mutableListOf<Day>()

    nextDays.add(Day(
        dayOfWeek = currentDate.dayOfWeek,
        dayOfMonth = currentDate.dayOfMonth
    ))

    for(i in 1..5) {
        val nextDay = currentDate.plusDays(1)
        nextDays.add(Day(
            dayOfWeek = nextDay.dayOfWeek,
            dayOfMonth = nextDay.dayOfMonth
        ))
    }

    return nextDays
}
