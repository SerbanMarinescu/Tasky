package com.example.tasky.common

import com.example.tasky.BuildConfig

object Constants {
    const val API_KEY = BuildConfig.API_KEY
    const val BASE_URL = "https://tasky.pl-coding.com/"
    const val DATABASE_NAME = "Agenda.db"

    const val NOTIFICATION_CHANNEL_ID = "channel_id"
    const val NOTIFICATION_CHANNEL_DESCRIPTION = "Used to remind the User of upcoming Events, Tasks and Reminders"
    const val APP_NAME = "Tasky"

    const val ACTION = "action"
    const val AGENDA_ITEM_TYPE = "agendaItemType"
    const val EVENT = "event"
    const val DELETED_PHOTOS = "deletedPhotos"
    const val REMINDER = "reminder"
    const val TASK = "task"
    const val DELETED_EVENT_IDS = "deletedEventIds"
    const val DELETED_REMINDER_IDS = "deletedReminderIds"
    const val DELETED_TASK_IDS = "deletedTaskIds"

    const val WORK_DATA_KEY = "workDataKey"

    const val TYPE_KEY = "TypeKey"
    const val TEXT_KEY = "TextKey"

    const val ROUTE_REGISTER = "RegisterScreen"
    const val ROUTE_LOGIN = "LoginScreen"
    const val ROUTE_AGENDA = "AgendaScreen"
    const val ROUTE_EVENT_DETAIL = "EventDetailScreen"
    const val ROUTE_TASK_DETAIL = "TaskDetailScreen"
    const val ROUTE_REMINDER_DETAIL = "ReminderDetailScreen"
    const val ROUTE_EDIT_DETAILS = "EditDetailsScreen"
    const val ROUTE_PHOTO_DETAIL = "PhotoDetailsScreen"
}