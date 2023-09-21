package com.example.tasky.presentation.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tasky.util.Screen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
        composable(Screen.SplashScreen.route) {

        }
        composable(Screen.RegisterScreen.route) {

        }
        composable(Screen.LoginScreen.route) {

        }
    }
}