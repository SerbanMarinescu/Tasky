package com.example.tasky.feature_agenda.presentation.agenda_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tasky.feature_agenda.presentation.agenda_screen.components.DatePickerDialog
import com.example.tasky.feature_agenda.presentation.agenda_screen.components.DayChip
import com.example.tasky.feature_agenda.presentation.agenda_screen.components.TopSection
import com.example.tasky.presentation.theme.BackgroundBlack
import com.example.tasky.presentation.theme.BackgroundWhite
import com.example.tasky.util.Result
import com.example.tasky.util.Screen
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AgendaScreen(
    navController: NavController,
    viewModel: AgendaViewModel = hiltViewModel()
) {
    viewModel.dateDialogState = rememberMaterialDialogState()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.logoutResult.collect { result ->
            when(result) {
                is Result.Error -> Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                is Result.Success -> navController.navigate(Screen.LoginScreen.route)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
    ) {
        TopSection(
            state = state,
            dateDialogState = viewModel.dateDialogState,
            username = viewModel.username,
           showMenuOptions = {
                viewModel.onEvent(AgendaEvent.ToggleLogoutBtn(it))
            },
            logout = {
                viewModel.onEvent(AgendaEvent.Logout)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(
                        topStart = 30.dp,
                        topEnd = 30.dp
                    )
                )
                .background(BackgroundWhite)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    state.daysList.forEachIndexed { index, day ->
                        DayChip(
                            dayOfWeek = day.dayOfWeek.toString(),
                            dayOfMonth = day.dayOfMonth.toString(),
                            selected = state.selectedDayIndex == index,
                            onClick = {
                                viewModel.onEvent(AgendaEvent.SelectDayIndex(index))
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = state.currentDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")))
                Spacer(modifier = Modifier.height(20.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth()) {

                }
            }
        }
    }

    DatePickerDialog(
        state = state,
        dialogState = viewModel.dateDialogState,
        onClick = {
            val date = it.atStartOfDay(ZoneId.systemDefault())
            viewModel.onEvent(AgendaEvent.SelectDate(date))
        }
    )
}