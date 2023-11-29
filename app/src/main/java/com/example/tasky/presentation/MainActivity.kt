package com.example.tasky.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tasky.feature_agenda.data.worker.SyncWorker
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.feature_authentication.domain.util.AuthUseCaseResult
import com.example.tasky.feature_authentication.presentation.login_screen.LoginViewModel
import com.example.tasky.presentation.Navigation.Navigation
import com.example.tasky.presentation.theme.TaskyTheme
import com.example.tasky.util.ArgumentTypeEnum
import com.example.tasky.util.Screen
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var workManager: WorkManager

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueue(syncRequest)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        setContent {
            TaskyTheme {
                val navController = rememberNavController()

                LaunchedEffect(key1 = true, intent) {

                    val itemType = intent.getParcelableExtra(ArgumentTypeEnum.TYPE.name, AgendaItemType::class.java)
                    val itemId = intent.getStringExtra(ArgumentTypeEnum.ITEM_ID.name)

                    viewModel.authResult.collect { result ->
                        when(result) {
                            is AuthUseCaseResult.GenericError -> navController.navigate(Screen.LoginScreen.route)
                            is AuthUseCaseResult.Success -> {
                                if(itemId != null && itemType != null) {
                                    val agendaItemScreen = when(itemType) {
                                        AgendaItemType.EVENT -> Screen.EventDetailScreen.route
                                        AgendaItemType.REMINDER -> Screen.ReminderDetailScreen.route
                                        AgendaItemType.TASK -> Screen.TaskDetailScreen.route

                                    }
                                    navController.navigate(agendaItemScreen + "?${ArgumentTypeEnum.ITEM_ID.name}=$itemId")
                                } else {
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



