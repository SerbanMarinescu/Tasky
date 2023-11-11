package com.example.tasky.presentation.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tasky.feature_agenda.presentation.agenda_screen.AgendaScreen
import com.example.tasky.feature_agenda.presentation.agenda_screen.AgendaViewModel
import com.example.tasky.feature_authentication.presentation.login_screen.LoginScreen
import com.example.tasky.feature_authentication.presentation.register_screen.RegisterScreen
import com.example.tasky.util.Screen
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

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
            val viewModel = hiltViewModel<AgendaViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val logoutEventsChannelFlow = viewModel.logoutResult
            viewModel.dateDialogState = rememberMaterialDialogState()

            AgendaScreen(
                navController = navController,
                state = state,
                onEvent = viewModel::onEvent,
                dateDialogState = viewModel.dateDialogState,
                username = viewModel.username,
                logoutEventsChannelFlow = logoutEventsChannelFlow
            )
        }
    }
}