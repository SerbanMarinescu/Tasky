package com.example.tasky.presentation.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tasky.feature_agenda.presentation.agenda_screen.AgendaScreen
import com.example.tasky.feature_agenda.presentation.agenda_screen.AgendaViewModel
import com.example.tasky.feature_agenda.presentation.edit_details_screen.EditDetailsScreen
import com.example.tasky.feature_agenda.presentation.edit_details_screen.EditDetailsViewModel
import com.example.tasky.feature_agenda.presentation.task_detail_screen.TaskDetailScreen
import com.example.tasky.feature_agenda.presentation.task_detail_screen.TaskDetailViewModel
import com.example.tasky.feature_authentication.presentation.login_screen.LoginScreen
import com.example.tasky.feature_authentication.presentation.register_screen.RegisterScreen
import com.example.tasky.util.ArgumentTypeEnum
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
                state = state,
                onEvent = viewModel::onEvent,
                dateDialogState = viewModel.dateDialogState,
                username = viewModel.username,
                logoutEventsChannelFlow = logoutEventsChannelFlow,
                navigateTo = {
                    navController.navigate(it)
                }
            )
        }
        composable(
            route = Screen.TaskDetailScreen.route +
                    "?${ArgumentTypeEnum.TASK_ID.name}={${ArgumentTypeEnum.TASK_ID.name}}" +
                    "&${ArgumentTypeEnum.TITLE.name}={${ArgumentTypeEnum.TITLE.name}}" +
                    "&${ArgumentTypeEnum.DESCRIPTION.name}={${ArgumentTypeEnum.DESCRIPTION.name}}",
            arguments = listOf(
                navArgument(name = ArgumentTypeEnum.TASK_ID.name) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },
                navArgument(name = ArgumentTypeEnum.TITLE.name) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },
                navArgument(name = ArgumentTypeEnum.DESCRIPTION.name) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
        ) {
            val viewModel = hiltViewModel<TaskDetailViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            TaskDetailScreen(
                state = state,
                onEvent = viewModel::onEvent,
                navigateTo = {
                    navController.navigate(it)
                },
                goBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.EditDetailsScreen.route +
                    "/{${ArgumentTypeEnum.TYPE}}" +
                    "/{${ArgumentTypeEnum.TEXT}}",
            arguments = listOf(
                navArgument(name = ArgumentTypeEnum.TYPE.name){
                    type = NavType.StringType
                },
                navArgument(name = ArgumentTypeEnum.TEXT.name) {
                    type = NavType.StringType
                }
            )
        ) {
            val viewModel = hiltViewModel<EditDetailsViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            EditDetailsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}