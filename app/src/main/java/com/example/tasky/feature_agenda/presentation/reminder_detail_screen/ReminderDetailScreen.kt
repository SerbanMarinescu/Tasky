package com.example.tasky.feature_agenda.presentation.reminder_detail_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.feature_agenda.domain.util.ReminderType
import com.example.tasky.feature_agenda.presentation.components.DatePickerDialog
import com.example.tasky.feature_agenda.presentation.components.TimePickerDialog
import com.example.tasky.feature_agenda.presentation.util.formatDateTimeOfPattern
import com.example.tasky.presentation.theme.BackgroundBlack
import com.example.tasky.presentation.theme.BackgroundWhite
import com.example.tasky.presentation.theme.Gray
import com.example.tasky.presentation.theme.Light
import com.example.tasky.presentation.theme.Light2
import com.example.tasky.presentation.theme.interFont
import com.example.tasky.util.ArgumentTypeEnum
import com.example.tasky.util.Screen
import com.vanpra.composematerialdialogs.MaterialDialogState
import java.time.format.DateTimeFormatter

@Composable
fun ReminderDetailScreen(
    state: ReminderDetailState,
    onEvent: (ReminderDetailEvent) -> Unit,
    dateDialogState: MaterialDialogState,
    timeDialogState: MaterialDialogState,
    navigateTo: (String) -> Unit,
    goBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundBlack)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    goBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                text = if (state.editMode) stringResource(id = R.string.EditReminder)
                else formatDateTimeOfPattern(state.currentDate, "dd MMMM yyyy"),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = interFont

            )
            if (state.editMode) {
                Text(
                    text = stringResource(id = R.string.Save),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable {
                        onEvent(ReminderDetailEvent.SaveReminder)
                        goBack()
                    }
                )
            } else {
                IconButton(
                    onClick = {
                        onEvent(ReminderDetailEvent.ToggleEditMode)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
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
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Square,
                        contentDescription = null,
                        tint = Light2
                    )
                    Text(
                        text = stringResource(id = R.string.Reminder),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 8.dp, end = 17.dp)
                        .run {
                            if (state.editMode) {
                                clickable {
                                    navigateTo(
                                        Screen.EditDetailsScreen.route +
                                                "/${ArgumentTypeEnum.TITLE.name}" +
                                                "/${state.reminderTitle}"
                                    )
                                }
                            } else {
                                this
                            }
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Outlined.Circle, contentDescription = null)
                        Text(
                            text = state.reminderTitle,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (state.editMode) {
                        Icon(imageVector = Icons.Default.ArrowRight, contentDescription = null)
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 17.dp, end = 17.dp, top = 8.dp),
                    color = Light
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, start = 17.dp, end = 17.dp)
                        .run {
                            if (state.editMode) {
                                clickable {
                                    navigateTo(
                                        Screen.EditDetailsScreen.route +
                                                "/${ArgumentTypeEnum.DESCRIPTION.name}" +
                                                "/${state.reminderDescription}"
                                    )
                                }
                            } else {
                                this
                            }
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = state.reminderDescription,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    }
                    if (state.editMode) {
                        Icon(
                            imageVector = Icons.Default.ArrowRight,
                            contentDescription = null
                        )
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 17.dp, end = 17.dp, top = 8.dp),
                    color = Light
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, start = 17.dp, end = 17.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.At),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = state.atTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.run {
                            if (state.editMode) {
                                clickable {
                                    timeDialogState.show()
                                }
                            } else {
                                this
                            }
                        }
                    )
                    Text(
                        text = state.atDate.format(DateTimeFormatter.ofPattern("MMM d yyyy")),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.run {
                            if (state.editMode) {
                                clickable {
                                    dateDialogState.show()
                                }
                            } else {
                                this
                            }
                        }
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 17.dp, end = 17.dp, top = 8.dp),
                    color = Light
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, start = 17.dp, end = 17.dp)
                        .run {
                            if (state.editMode) {
                                clickable {
                                    onEvent(ReminderDetailEvent.ToggleReminderMenu)
                                }
                            } else {
                                this
                            }
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            modifier = Modifier.background(Light2),
                            tint = Gray
                        )
                        Text(
                            text = when(state.reminderType) {
                                ReminderType.TEN_MINUTES_BEFORE -> stringResource(id = R.string.TenMinBefore)
                                ReminderType.THIRTY_MINUTES_BEFORE -> stringResource(id = R.string.ThirtyMinBefore)
                                ReminderType.ONE_HOUR_BEFORE -> stringResource(id = R.string.OneHourBefore)
                                ReminderType.SIX_HOURS_BEFORE -> stringResource(id = R.string.SixHoursBefore)
                                ReminderType.ONE_DAY_BEFORE -> stringResource(id = R.string.OneDayBefore)
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    DropdownMenu(
                        expanded = state.isReminderMenuVisible,
                        onDismissRequest = {
                            onEvent(ReminderDetailEvent.ToggleReminderMenu)
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = R.string.TenMinBefore))
                            },
                            onClick = {
                                onEvent(ReminderDetailEvent.ReminderTypeChanged(ReminderType.TEN_MINUTES_BEFORE))
                                onEvent(ReminderDetailEvent.ToggleReminderMenu)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = R.string.ThirtyMinBefore))
                            },
                            onClick = {
                                onEvent(ReminderDetailEvent.ReminderTypeChanged(ReminderType.THIRTY_MINUTES_BEFORE))
                                onEvent(ReminderDetailEvent.ToggleReminderMenu)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = R.string.OneHourBefore))
                            },
                            onClick = {
                                onEvent(ReminderDetailEvent.ReminderTypeChanged(ReminderType.ONE_HOUR_BEFORE))
                                onEvent(ReminderDetailEvent.ToggleReminderMenu)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = R.string.SixHoursBefore))
                            },
                            onClick = {
                                onEvent(ReminderDetailEvent.ReminderTypeChanged(ReminderType.SIX_HOURS_BEFORE))
                                onEvent(ReminderDetailEvent.ToggleReminderMenu)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = R.string.OneDayBefore))
                            },
                            onClick = {
                                onEvent(ReminderDetailEvent.ReminderTypeChanged(ReminderType.ONE_DAY_BEFORE))
                                onEvent(ReminderDetailEvent.ToggleReminderMenu)
                            }
                        )
                    }

                    if(state.editMode) {
                        Icon(imageVector = Icons.Default.ArrowRight, contentDescription = null)
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 17.dp, end = 17.dp, top = 8.dp),
                    color = Light
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 70.dp)
                    .clickable {
                        onEvent(ReminderDetailEvent.DeleteReminder)
                        goBack()
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 17.dp, end = 17.dp, top = 8.dp),
                    color = Light
                )
                Text(
                    text = stringResource(id = R.string.DeleteReminderCaps),
                    color = Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    DatePickerDialog(
        initialDate = state.atDate,
        dialogState = dateDialogState,
        onClick = {
            onEvent(ReminderDetailEvent.AtDateChanged(it))
        }
    )
    TimePickerDialog(
        initialTime = state.atTime,
        dialogState = timeDialogState,
        onClick = {
            onEvent(ReminderDetailEvent.AtTimeChanged(it))
        }
    )
}