package com.example.tasky.presentation.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.tasky.feature_agenda.presentation.reminder_detail_screen.ReminderDetailEvent
import com.example.tasky.feature_agenda.presentation.reminder_detail_screen.ReminderDetailScreen
import com.example.tasky.feature_agenda.presentation.reminder_detail_screen.ReminderDetailViewModel
import com.example.tasky.feature_agenda.presentation.task_detail_screen.TaskDetailEvent
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
                    "?${ArgumentTypeEnum.ITEM_ID.name}={${ArgumentTypeEnum.ITEM_ID.name}}" +
                    "&${ArgumentTypeEnum.TYPE.name}={${ArgumentTypeEnum.TYPE.name}}" +
                    "&${ArgumentTypeEnum.TEXT.name}={${ArgumentTypeEnum.TEXT.name}}" +
                    "&${ArgumentTypeEnum.EDIT_MODE.name}={${ArgumentTypeEnum.EDIT_MODE.name}}",
            arguments = listOf(
                navArgument(name = ArgumentTypeEnum.ITEM_ID.name) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },
                navArgument(name = ArgumentTypeEnum.TYPE.name) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },
                navArgument(name = ArgumentTypeEnum.TEXT.name) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },
                navArgument(name = ArgumentTypeEnum.EDIT_MODE.name) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
        ) { entry ->
            val viewModel = hiltViewModel<TaskDetailViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            viewModel.dateDialogState = rememberMaterialDialogState()
            viewModel.timeDialogState = rememberMaterialDialogState()

            LaunchedEffect(key1 = entry.savedStateHandle) {
                val type = entry.savedStateHandle.get<String>(ArgumentTypeEnum.TYPE.name)
                val text = entry.savedStateHandle.get<String>(ArgumentTypeEnum.TEXT.name)

                type?.let {
                    text?.let {
                        if(type == ArgumentTypeEnum.TITLE.name) {
                            viewModel.onEvent(TaskDetailEvent.TitleChanged(text))
                        }
                        if(type == ArgumentTypeEnum.DESCRIPTION.name) {
                            viewModel.onEvent(TaskDetailEvent.DescriptionChanged(text))
                        }
                    }
                }
            }

            TaskDetailScreen(
                state = state,
                dateDialogState = viewModel.dateDialogState,
                timeDialogState = viewModel.timeDialogState,
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
            route = Screen.ReminderDetailScreen.route +
                    "?${ArgumentTypeEnum.ITEM_ID.name}={${ArgumentTypeEnum.ITEM_ID.name}}" +
                    "&${ArgumentTypeEnum.TYPE.name}={${ArgumentTypeEnum.TYPE.name}}" +
                    "&${ArgumentTypeEnum.TEXT.name}={${ArgumentTypeEnum.TEXT.name}}" +
                    "&${ArgumentTypeEnum.EDIT_MODE.name}={${ArgumentTypeEnum.EDIT_MODE.name}}",
            arguments = listOf(
                navArgument(name = ArgumentTypeEnum.ITEM_ID.name) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },
                navArgument(name = ArgumentTypeEnum.TYPE.name) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },
                navArgument(name = ArgumentTypeEnum.TEXT.name) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },
                navArgument(name = ArgumentTypeEnum.EDIT_MODE.name) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
        ) { entry ->
            val viewModel = hiltViewModel<ReminderDetailViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            viewModel.dateDialogState = rememberMaterialDialogState()
            viewModel.timeDialogState = rememberMaterialDialogState()

            LaunchedEffect(key1 = entry.savedStateHandle) {
                val type = entry.savedStateHandle.get<String>(ArgumentTypeEnum.TYPE.name)
                val text = entry.savedStateHandle.get<String>(ArgumentTypeEnum.TEXT.name)

                type?.let {
                    text?.let {
                        if(type == ArgumentTypeEnum.TITLE.name) {
                            viewModel.onEvent(ReminderDetailEvent.TitleChanged(text))
                        }
                        if(type == ArgumentTypeEnum.DESCRIPTION.name) {
                            viewModel.onEvent(ReminderDetailEvent.DescriptionChanged(text))
                        }
                    }
                }
            }

            ReminderDetailScreen(
                state = state,
                onEvent = viewModel::onEvent,
                dateDialogState = viewModel.dateDialogState,
                timeDialogState = viewModel.timeDialogState,
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

            LaunchedEffect(key1 = state.contentSaved) {
                if(state.contentSaved) {
                    navController.previousBackStackEntry?.savedStateHandle?.set(ArgumentTypeEnum.TYPE.name, state.type)
                    navController.previousBackStackEntry?.savedStateHandle?.set(ArgumentTypeEnum.TEXT.name, state.content)
                    navController.popBackStack()
                }
            }

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