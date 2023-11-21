package com.example.tasky.util

import com.example.tasky.common.Constants.ROUTE_AGENDA
import com.example.tasky.common.Constants.ROUTE_EDIT_DETAILS
import com.example.tasky.common.Constants.ROUTE_EVENT_DETAIL
import com.example.tasky.common.Constants.ROUTE_LOGIN
import com.example.tasky.common.Constants.ROUTE_PHOTO_DETAIL
import com.example.tasky.common.Constants.ROUTE_REGISTER
import com.example.tasky.common.Constants.ROUTE_REMINDER_DETAIL
import com.example.tasky.common.Constants.ROUTE_TASK_DETAIL

sealed class Screen(val route: String) {
    data object RegisterScreen: Screen(ROUTE_REGISTER)
    data object LoginScreen: Screen(ROUTE_LOGIN)
    data object AgendaScreen: Screen(ROUTE_AGENDA)
    data object EventDetailScreen: Screen(ROUTE_EVENT_DETAIL)
    data object TaskDetailScreen: Screen(ROUTE_TASK_DETAIL)
    data object ReminderDetailScreen: Screen(ROUTE_REMINDER_DETAIL)
    data object EditDetailsScreen: Screen(ROUTE_EDIT_DETAILS)
    data object PhotoDetailsScreen: Screen(ROUTE_PHOTO_DETAIL)
}
