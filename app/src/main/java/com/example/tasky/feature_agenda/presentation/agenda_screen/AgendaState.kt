package com.example.tasky.feature_agenda.presentation.agenda_screen

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.presentation.util.Day
import com.example.tasky.feature_agenda.presentation.util.generateNextDays
import java.time.Month
import java.time.ZonedDateTime

data class AgendaState(
    val itemList: List<AgendaItem> = emptyList(),
    val selectedDayIndex: Int = 0,
    val currentMonth: Month = ZonedDateTime.now().month,
    val daysList: List<Day> = generateNextDays(ZonedDateTime.now()),
    val currentDate: ZonedDateTime = ZonedDateTime.now(),
    val showLogoutOption: Boolean = false
)
