package com.example.tasky.presentation.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tasky.feature_authentication.presentation.login_screen.LoginScreen
import com.example.tasky.feature_authentication.presentation.register_screen.RegisterScreen
import com.example.tasky.util.Screen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(Screen.RegisterScreen.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.AgendaScreen.route) {

        }
    }
}