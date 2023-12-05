package com.example.tasky.common

import com.example.tasky.BuildConfig

object Constants {
    const val API_KEY = BuildConfig.API_KEY
    const val BASE_URL = "https://tasky.pl-coding.com/"
    const val DATABASE_NAME = "Agenda.db"

    const val NOTIFICATION_CHANNEL_ID = "channel_id"
    const val APP_NAME = "Tasky"
    const val APP_SCHEME = "tasky"
    const val EVENT_HOST = "event_detail"
    const val REMINDER_HOST = "reminder_detail"
    const val TASK_HOST = "task_detail"
    const val DEEP_LINK_HANDLER = "DeepLinkHandler"

    const val WORK_DATA_KEY = "workDataKey"

    const val ROUTE_REGISTER = "RegisterScreen"
    const val ROUTE_LOGIN = "LoginScreen"
    const val ROUTE_AGENDA = "AgendaScreen"
    const val ROUTE_EVENT_DETAIL = "EventDetailScreen"
    const val ROUTE_TASK_DETAIL = "TaskDetailScreen"
    const val ROUTE_REMINDER_DETAIL = "ReminderDetailScreen"
    const val ROUTE_EDIT_DETAILS = "EditDetailsScreen"
    const val ROUTE_PHOTO_DETAIL = "PhotoDetailsScreen"
}