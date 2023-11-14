package com.example.tasky.feature_agenda.presentation.agenda_screen

import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.util.AgendaItemKey
import com.example.tasky.feature_agenda.domain.util.toAgendaItemType
import com.example.tasky.feature_agenda.presentation.agenda_screen.components.AgendaItemCard
import com.example.tasky.feature_agenda.presentation.agenda_screen.components.DatePickerDialog
import com.example.tasky.feature_agenda.presentation.agenda_screen.components.DayChip
import com.example.tasky.feature_agenda.presentation.agenda_screen.components.TopSection
import com.example.tasky.feature_agenda.presentation.util.formatDateTimeOfPattern
import com.example.tasky.presentation.theme.BackgroundBlack
import com.example.tasky.presentation.theme.BackgroundWhite
import com.example.tasky.util.ArgumentTypeEnum
import com.example.tasky.util.ObserveAsEvents
import com.example.tasky.util.Result
import com.example.tasky.util.Screen
import com.vanpra.composematerialdialogs.MaterialDialogState
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    state: AgendaState,
    onEvent: (AgendaEvent) -> Unit,
    dateDialogState: MaterialDialogState,
    username: String,
    logoutEventsChannelFlow: Flow<Result<Unit>>,
    navigateTo: (String) -> Unit
) {
    val context = LocalContext.current

    ObserveAsEvents(logoutEventsChannelFlow) { result ->
        when(result) {
            is Result.Error -> Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
            is Result.Success -> navigateTo(Screen.LoginScreen.route)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(AgendaEvent.ToggleItemCreationMenu)
            }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.CreateAgendaItem)
                )
                DropdownMenu(
                    expanded = state.isItemCreationMenuVisible,
                    onDismissRequest = {
                        onEvent(AgendaEvent.ToggleItemCreationMenu)
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.AgendaMenu_Event))
                    },
                        onClick = {
                            TODO("Navigate to EventDetailScreen")
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.AgendaMenu_Reminder))
                        },
                        onClick = {
                            TODO("Navigate to ReminderDetailScreen")
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.AgendaMenu_Task))
                        },
                        onClick = {
                            Log.d("NAV", "Clicked on create new task")
                            navigateTo(Screen.TaskDetailScreen.route)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundBlack)
        ) {
            TopSection(
                state = state,
                dateDialogState = dateDialogState,
                username = username,
                showMenuOptions = {
                    onEvent(AgendaEvent.ToggleLogoutBtn)
                },
                logout = {
                    onEvent(AgendaEvent.Logout)
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
                                dayOfWeek = day.dayOfWeek.toString().take(1),
                                dayOfMonth = day.dayOfMonth.toString(),
                                selected = state.selectedDayIndex == index,
                                onClick = {
                                    onEvent(AgendaEvent.SelectDayIndex(index))
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = formatDateTimeOfPattern(state.currentDate, "dd MMMM yyyy"))
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(state.itemList) { agendaItem ->
                            when(agendaItem) {
                                is AgendaItem.Event -> {
                                    AgendaItemCard(
                                        item = agendaItem,
                                        timeRange = "${formatDateTimeOfPattern(agendaItem.from, "MMM d, HH:mm")} - ${formatDateTimeOfPattern(agendaItem.to, "MMM d, HH:mm")}",
                                        onOpenClick = {
                                            TODO("Navigate to EventDetailScreen")
                                        },
                                        onEditClick = {
                                            TODO("Navigate to EventDetailScreen")
                                        },
                                        onDeleteClick = {
                                            onEvent(AgendaEvent.DeleteItem(agendaItem))
                                        },
                                        isMenuVisible = state.isItemMenuVisible[AgendaItemKey(agendaItem.toAgendaItemType(), agendaItem.id)] ?: false,
                                        onMenuClick = { itemKey, isVisible ->
                                            onEvent(AgendaEvent.ToggleIndividualItemMenu(itemKey, isVisible))
                                        },
                                        isDeletionDialogVisible = state.isDeletionDialogVisible,
                                        toggleDeletionDialog = {
                                            onEvent(AgendaEvent.ToggleDeletionDialog)
                                        }
                                    )
                                }
                                is AgendaItem.Reminder -> {
                                    AgendaItemCard(
                                        item = agendaItem,
                                        timeRange = formatDateTimeOfPattern(agendaItem.time, "MMM d, HH:mm"),
                                        onOpenClick = {
                                            TODO("Navigate to ReminderDetailScreen")
                                        },
                                        onEditClick = {
                                            TODO("Navigate to ReminderDetailScreen")
                                        },
                                        onDeleteClick = {
                                            onEvent(AgendaEvent.DeleteItem(agendaItem))
                                        },
                                        isMenuVisible = state.isItemMenuVisible[AgendaItemKey(agendaItem.toAgendaItemType(), agendaItem.id)] ?: false,
                                        onMenuClick = { itemKey, isVisible ->
                                            onEvent(AgendaEvent.ToggleIndividualItemMenu(itemKey, isVisible))
                                        },
                                        isDeletionDialogVisible = state.isDeletionDialogVisible,
                                        toggleDeletionDialog = {
                                            onEvent(AgendaEvent.ToggleDeletionDialog)
                                        }
                                    )
                                }
                                is AgendaItem.Task -> {
                                    Log.d("MENU", "Composition triggered for task")
                                    AgendaItemCard(
                                        item = agendaItem,
                                        timeRange = formatDateTimeOfPattern(agendaItem.time, "MMM d, HH:mm"),
                                        onOpenClick = {
                                            Log.d("NAV", "Clicked on open task")
                                            navigateTo(
                                                Screen.TaskDetailScreen.route +
                                                "?${ArgumentTypeEnum.TASK_ID.name}=${agendaItem.taskId}"
                                            )
                                        },
                                        onEditClick = {
                                            TODO("Navigate to TaskDetailScreen")
                                        },
                                        onDeleteClick = {
                                            onEvent(AgendaEvent.DeleteItem(agendaItem))
                                        },
                                        isMenuVisible = state.isItemMenuVisible[AgendaItemKey(agendaItem.toAgendaItemType(), agendaItem.taskId)] ?: false,
                                        onMenuClick = { itemKey, isVisible ->
                                            Log.d("MENU", "On menu click triggered")
                                            onEvent(AgendaEvent.ToggleIndividualItemMenu(itemKey, isVisible))
                                        },
                                        isDeletionDialogVisible = state.isDeletionDialogVisible,
                                        toggleDeletionDialog = {
                                            onEvent(AgendaEvent.ToggleDeletionDialog)
                                        },
                                        selected = agendaItem.isDone,
                                        toggleIsDone = {
                                            onEvent(AgendaEvent.ToggleIsDone(agendaItem))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    DatePickerDialog(
        state = state,
        dialogState = dateDialogState,
        onClick = {
            val date = it.atStartOfDay(ZoneId.systemDefault())
            onEvent(AgendaEvent.SelectDate(date))
        }
    )
}