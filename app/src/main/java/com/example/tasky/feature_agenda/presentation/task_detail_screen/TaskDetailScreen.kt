package com.example.tasky.feature_agenda.presentation.task_detail_screen

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.tasky.feature_agenda.presentation.util.formatDateTimeOfPattern
import com.example.tasky.presentation.theme.BackgroundBlack
import com.example.tasky.presentation.theme.BackgroundWhite
import com.example.tasky.presentation.theme.Gray
import com.example.tasky.presentation.theme.Green
import com.example.tasky.presentation.theme.Light
import com.example.tasky.presentation.theme.Light2
import com.example.tasky.presentation.theme.interFont
import com.example.tasky.util.ArgumentTypeEnum
import com.example.tasky.util.Screen
import java.time.format.DateTimeFormatter

@Composable
fun TaskDetailScreen(
    state: TaskDetailState,
    onEvent: (TaskDetailEvent) -> Unit,
    taskId: String?,
    newTitle: String?,
    newDescription: String?,
    navigateTo: (String) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        taskId?.let {
            onEvent(TaskDetailEvent.GetOpenedTask(it))
        }
        newTitle?.let {
            onEvent(TaskDetailEvent.TitleChanged(it))
        }
        newDescription?.let {
            onEvent(TaskDetailEvent.DescriptionChanged(it))
        }
    }

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
                    navigateTo(Screen.AgendaScreen.route)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                text = if (state.editMode) stringResource(id = R.string.EditTask)
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
                        onEvent(TaskDetailEvent.CreateTask)
                    }
                )
            } else {
                IconButton(
                    onClick = {
                        onEvent(TaskDetailEvent.ToggleEditMode)
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
                        tint = Green
                    )
                    Text(
                        text = stringResource(id = R.string.Task),
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
                                                "/${ArgumentTypeEnum.TYPE.name}=${ArgumentTypeEnum.TITLE.name}" +
                                                "/${ArgumentTypeEnum.TEXT.name}=${state.taskTitle}"
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
                            text = state.taskTitle,
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
                                                "/${ArgumentTypeEnum.TYPE.name}=${ArgumentTypeEnum.DESCRIPTION.name}" +
                                                "/${ArgumentTypeEnum.TEXT.name}=${state.taskDescription}"
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
                            text = state.taskDescription,
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
                                    TODO("Open time picker dialog")
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
                                    TODO("Open date picker dialog")
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
                                    TODO("Open drop down menu")
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
                        onEvent(TaskDetailEvent.DeleteTask(taskId))
                        navigateTo(Screen.AgendaScreen.route)
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
                    text = stringResource(id = R.string.DELETE_TASK),
                    color = Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
