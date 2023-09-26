package com.example.tasky.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.tasky.feature_authentication.domain.util.AuthUseCaseResult
import com.example.tasky.feature_authentication.presentation.login_screen.LoginViewModel
import com.example.tasky.presentation.Navigation.Navigation
import com.example.tasky.presentation.theme.TaskyTheme
import com.example.tasky.util.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        setContent {
            TaskyTheme {
                val navController = rememberNavController()
                LaunchedEffect(key1 = true) {
                    viewModel.authResult.collect { result ->
                        when(result) {
                            is AuthUseCaseResult.GenericError -> navController.navigate(Screen.LoginScreen.route)
                            is AuthUseCaseResult.Success -> TODO("Navigate to Agenda Screen")
                            else -> Unit
                        }
                    }
                }
                Navigation(navController = navController)
            }
        }
    }
}



