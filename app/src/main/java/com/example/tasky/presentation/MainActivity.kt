package com.example.tasky.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.tasky.presentation.Navigation.Navigation
import com.example.tasky.presentation.theme.TaskyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskyTheme {
                val navController = rememberNavController()
                Navigation(navController = navController)
            }
        }
    }
}



