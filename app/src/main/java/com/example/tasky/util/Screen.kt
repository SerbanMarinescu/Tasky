package com.example.tasky.util

import com.example.tasky.common.Constants.ROUTE_AGENDA
import com.example.tasky.common.Constants.ROUTE_LOGIN
import com.example.tasky.common.Constants.ROUTE_REGISTER

sealed class Screen(val route: String) {
    data object RegisterScreen: Screen(ROUTE_REGISTER)
    data object LoginScreen: Screen(ROUTE_LOGIN)
    data object AgendaScreen: Screen(ROUTE_AGENDA)
}
