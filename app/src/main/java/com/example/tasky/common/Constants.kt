package com.example.tasky.common

import com.example.tasky.BuildConfig

object Constants {
    const val API_KEY = BuildConfig.API_KEY
    const val BASE_URL = "https://tasky.pl-coding.com/"
    const val DATABASE_NAME = "Agenda.db"

    const val ACTION = "action"
    const val EVENT = "event"
    const val DELETED_PHOTOS = "deletedPhotos"

    const val WORK_DATA_KEY = "workDataKey"

    const val ROUTE_REGISTER = "RegisterScreen"
    const val ROUTE_LOGIN = "LoginScreen"
    const val ROUTE_AGENDA = "AgendaScreen"
}