package com.example.tasky.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.tasky.common.Constants.DEEP_LINK_HANDLER
import com.example.tasky.feature_authentication.domain.util.AuthUseCaseResult
import com.example.tasky.feature_authentication.presentation.login_screen.LoginViewModel
import com.example.tasky.presentation.Navigation.Navigation
import com.example.tasky.presentation.theme.TaskyTheme
import com.example.tasky.util.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var navController: NavHostController

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        setContent {
            TaskyTheme {
                navController = rememberNavController()

                LaunchedEffect(key1 = true) {
                    viewModel.authResult.collect { result ->
                        when(result) {
                            is AuthUseCaseResult.GenericError -> navController.navigate(Screen.LoginScreen.route)
                            is AuthUseCaseResult.Success -> {
                                val shouldNavigateWithDeepLink = intent.getBooleanExtra(DEEP_LINK_HANDLER, false)

                                if(!shouldNavigateWithDeepLink) {
                                    navController.navigate(Screen.AgendaScreen.route)
                                }
                            }
                            else -> Unit
                        }
                    }
                }
                Navigation(navController = navController)
            }
        }
    }
}



