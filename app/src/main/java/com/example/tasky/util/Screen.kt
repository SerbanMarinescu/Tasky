package com.example.tasky.util

import com.example.tasky.common.Constants.ROUTE_LOGIN
import com.example.tasky.common.Constants.ROUTE_REGISTER

sealed class Screen(val route: String) {
    object RegisterScreen: Screen(ROUTE_REGISTER)
    object LoginScreen: Screen(ROUTE_LOGIN)
}
