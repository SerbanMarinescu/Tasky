package com.example.tasky.feature_agenda.presentation.event_detail_screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tasky.R
import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.model.EventPhoto
import com.example.tasky.feature_agenda.domain.util.ReminderType
import com.example.tasky.feature_agenda.presentation.components.DatePickerDialog
import com.example.tasky.feature_agenda.presentation.components.TimePickerDialog
import com.example.tasky.feature_agenda.presentation.event_detail_screen.components.AttendeeItem
import com.example.tasky.feature_agenda.presentation.event_detail_screen.components.FilterChip
import com.example.tasky.feature_agenda.presentation.util.DateTimeDialogType
import com.example.tasky.feature_agenda.presentation.util.EventOptions
import com.example.tasky.feature_agenda.presentation.util.SelectableChipOptions
import com.example.tasky.feature_agenda.presentation.util.formatDateTimeOfPattern
import com.example.tasky.feature_agenda.presentation.util.getInitials
import com.example.tasky.feature_authentication.presentation.components.TaskyTextField
import com.example.tasky.presentation.theme.BackgroundBlack
import com.example.tasky.presentation.theme.BackgroundWhite
import com.example.tasky.presentation.theme.DarkGray
import com.example.tasky.presentation.theme.Gray
import com.example.tasky.presentation.theme.Light
import com.example.tasky.presentation.theme.Light2
import com.example.tasky.presentation.theme.LightBlue
import com.example.tasky.presentation.theme.LightGreen
import com.example.tasky.presentation.theme.interFont
import com.example.tasky.util.ArgumentTypeEnum
import com.example.tasky.util.ObserveAsEvents
import com.example.tasky.util.Result
import com.example.tasky.util.Screen
import com.example.tasky.util.applyIf
import com.vanpra.composematerialdialogs.MaterialDialogState
import kotlinx.coroutines.flow.Flow
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    state: EventDetailState,
    onEvent: (EventDetailOnClick) -> Unit,
    photoPicker: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    dateDialogState: MaterialDialogState,
    timeDialogState: MaterialDialogState,
    validationResult: Flow<Result<Unit>>,
    photoList: List<EventPhoto>,
    attendeeList: List<Attendee>,
    navigateBack: () -> Unit,
    navigateTo: (String) -> Unit
) {
    val context = LocalContext.current

    ObserveAsEvents(flow = validationResult) { result ->
        when(result) {
            is Result.Error -> Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
            is Result.Success -> navigateBack()
        }
    }

    if(state.isLoading) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(20.dp)
                ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            progress = state.loadingProgress
                        )
                }
            },
            text = {
                   Text(
                       text = "Creating Event, please wait...",
                       fontSize = 16.sp,
                       fontWeight = FontWeight.Bold,
                       fontFamily = interFont,
                   )
            },
            confirmButton = {}
        )
    }

    if(state.addingAttendees) {
        AlertDialog(
            onDismissRequest = {
                onEvent(EventDetailOnClick.ToggleAddingAttendeeDialog)
                onEvent(EventDetailOnClick.AttendeeEmailChanged(""))
            },
            confirmButton = {
                    Button(
                        onClick = {
                            onEvent(EventDetailOnClick.AddAttendee(state.attendeeEmail))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            ,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BackgroundBlack
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.Add),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = interFont,
                            color = Color.White
                        )
                    }


            },
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.AddVisitor),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = interFont,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable {
                                onEvent(EventDetailOnClick.ToggleAddingAttendeeDialog)
                                onEvent(EventDetailOnClick.AttendeeEmailChanged(""))
                            }
                    )
                }

            },
            text = {
                TaskyTextField(
                    value = state.attendeeEmail,
                    onValueChanged = {
                        onEvent(EventDetailOnClick.AttendeeEmailChanged(it))
                    },
                    hint = stringResource(id = R.string.EmailHint),
                    keyboardType = KeyboardType.Email,
                    isValid = state.isEmailValid,
                    isError = state.emailError != null,
                    errorMessage = state.emailError?.asString(context)
                )
            }
        )
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
                    navigateBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                text = if(state.isInEditMode && state.isUserEventCreator) stringResource(id = R.string.EditEvent) else
                    formatDateTimeOfPattern(state.currentDate, "dd MMMM yyyy"),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = interFont

            )
            if (state.isInEditMode) {
                Text(
                    text = stringResource(id = R.string.Save),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable {
                        onEvent(EventDetailOnClick.SaveEvent)
                    }
                )
            } else {
                IconButton(
                    onClick = {
                        onEvent(EventDetailOnClick.ToggleEditMode)
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
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                item {
                    Column(Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Square,
                                contentDescription = null,
                                tint = LightGreen
                            )
                            Text(
                                text = stringResource(id = R.string.Event),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = interFont
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, top = 8.dp, end = 17.dp)
                                .applyIf(
                                    condition = state.isInEditMode && state.isUserEventCreator,
                                    modifier = Modifier.clickable {
                                        navigateTo(
                                            Screen.EditDetailsScreen.route +
                                                    "/${ArgumentTypeEnum.TITLE.name}" +
                                                    "/${state.eventTitle}"
                                        )
                                    }
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Outlined.Circle, contentDescription = null)
                                Text(
                                    text = state.eventTitle,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = interFont
                                )
                            }
                            if (state.isInEditMode && state.isUserEventCreator) {
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
                                .padding(top = 25.dp, start = 17.dp, end = 17.dp)
                                .applyIf(
                                    condition = state.isInEditMode && state.isUserEventCreator,
                                    modifier = Modifier.clickable {
                                        navigateTo(
                                            Screen.EditDetailsScreen.route +
                                                    "/${ArgumentTypeEnum.DESCRIPTION.name}" +
                                                    "/${state.eventDescription}"
                                        )
                                    }
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = state.eventDescription,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            }
                            if (state.isInEditMode && state.isUserEventCreator) {
                                Icon(
                                    imageVector = Icons.Default.ArrowRight,
                                    contentDescription = null
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        if (state.addingPhotos) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Light2)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.Photos),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = interFont
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        photoList.forEach { photo ->
                                            Box(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .border(
                                                        3.dp,
                                                        color = LightBlue,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                            ) {
                                                AsyncImage(
                                                    model = when (photo) {
                                                        is EventPhoto.Local -> photo.uri.toUri()
                                                        is EventPhoto.Remote -> {
                                                            ImageRequest.Builder(context)
                                                                .data(photo.url)
                                                                .build()
                                                        }
                                                    },
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .clickable {
                                                            navigateTo(
                                                                Screen.PhotoDetailsScreen.route +
                                                                        "/${
                                                                            when (photo) {
                                                                                is EventPhoto.Local -> photo.key
                                                                                is EventPhoto.Remote -> photo.key
                                                                            }
                                                                        }" +
                                                                        "/${
                                                                            Uri.encode(
                                                                                when (photo) {
                                                                                    is EventPhoto.Local -> photo.uri
                                                                                    is EventPhoto.Remote -> photo.url
                                                                                }
                                                                            )
                                                                        }"
                                                            )
                                                        }
                                                )
                                            }
                                        }
                                        if (photoList.size < 10 && state.isInEditMode && state.isUserEventCreator) {
                                            Box(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .border(
                                                        3.dp,
                                                        color = LightBlue,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .background(Light2)
                                                        .align(Alignment.Center)
                                                        .clickable {
                                                            photoPicker.launch(
                                                                PickVisualMediaRequest(
                                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                                )
                                                            )
                                                        },
                                                    tint = Gray
                                                )
                                            }

                                        }

                                    }
                                }
                            }
                        } else {
                            if(state.isUserEventCreator) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .background(Light2)
                                        .applyIf(
                                            condition = state.isInEditMode,
                                            modifier = Modifier.clickable {
                                                photoPicker.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                                onEvent(EventDetailOnClick.ToggleAddingPhotos)
                                            }
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Gray
                                        )
                                        Text(
                                            text = stringResource(id = R.string.AddPhotos),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = interFont,
                                            color = Gray
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 17.dp, end = 17.dp, top = 8.dp),
                            color = Light
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.5f)
                                    .padding(start = 17.dp, end = 17.dp),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.From),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = interFont,
                                    modifier = Modifier.align(Alignment.TopStart)
                                )
                                Text(
                                    text = state.fromTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = interFont,
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .applyIf(
                                            condition = state.isInEditMode && state.isUserEventCreator,
                                            modifier = Modifier.clickable {
                                                onEvent(
                                                    EventDetailOnClick.DateTimePickerChanged(
                                                        DateTimeDialogType.FROM_TIME
                                                    )
                                                )
                                                timeDialogState.show()
                                            }
                                        )
                                )
                                if (state.isInEditMode && state.isUserEventCreator) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.5f)
                                    .padding(start = 17.dp, end = 17.dp),
                            ) {
                                Text(
                                    text = state.fromDate.format(DateTimeFormatter.ofPattern("MMM d yyyy")),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = interFont,
                                    modifier = Modifier.applyIf(
                                        condition = state.isInEditMode && state.isUserEventCreator,
                                        modifier = Modifier
                                            .clickable {
                                                onEvent(
                                                    EventDetailOnClick.DateTimePickerChanged(
                                                        DateTimeDialogType.FROM_DATE
                                                    )
                                                )
                                                dateDialogState.show()
                                            }
                                            .align(Alignment.TopStart)
                                    )
                                )
                                if (state.isInEditMode && state.isUserEventCreator) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    )
                                }
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
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.5f)
                                    .padding(start = 17.dp, end = 17.dp),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.To),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = interFont,
                                    modifier = Modifier.align(Alignment.TopStart)
                                )
                                Text(
                                    text = state.toTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = interFont,
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .applyIf(
                                            condition = state.isInEditMode && state.isUserEventCreator,
                                            modifier = Modifier.clickable {
                                                onEvent(
                                                    EventDetailOnClick.DateTimePickerChanged(
                                                        DateTimeDialogType.TO_TIME
                                                    )
                                                )
                                                timeDialogState.show()
                                            }
                                        )
                                )
                                if (state.isInEditMode && state.isUserEventCreator) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.5f)
                                    .padding(start = 17.dp, end = 17.dp),
                            ) {
                                Text(
                                    text = state.toDate.format(DateTimeFormatter.ofPattern("MMM d yyyy")),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = interFont,
                                    modifier = Modifier.applyIf(
                                        condition = state.isInEditMode && state.isUserEventCreator,
                                        modifier = Modifier
                                            .clickable {
                                                onEvent(
                                                    EventDetailOnClick.DateTimePickerChanged(
                                                        DateTimeDialogType.TO_DATE
                                                    )
                                                )
                                                dateDialogState.show()
                                            }
                                            .align(Alignment.TopStart)
                                    )
                                )
                                if (state.isInEditMode && state.isUserEventCreator) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    )
                                }
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
                                .applyIf(
                                    condition = state.isInEditMode,
                                    modifier = Modifier.clickable {
                                        onEvent(EventDetailOnClick.ToggleReminderMenu)
                                    }
                                ),
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
                                    text = when (state.reminderType) {
                                        ReminderType.TEN_MINUTES_BEFORE -> stringResource(id = R.string.TenMinBefore)
                                        ReminderType.THIRTY_MINUTES_BEFORE -> stringResource(id = R.string.ThirtyMinBefore)
                                        ReminderType.ONE_HOUR_BEFORE -> stringResource(id = R.string.OneHourBefore)
                                        ReminderType.SIX_HOURS_BEFORE -> stringResource(id = R.string.SixHoursBefore)
                                        ReminderType.ONE_DAY_BEFORE -> stringResource(id = R.string.OneDayBefore)
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = interFont
                                )
                            }

                            DropdownMenu(
                                expanded = state.isReminderMenuVisible,
                                onDismissRequest = {
                                    onEvent(EventDetailOnClick.ToggleReminderMenu)
                                }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(id = R.string.TenMinBefore))
                                    },
                                    onClick = {
                                        onEvent(EventDetailOnClick.ReminderTypeChanged(ReminderType.TEN_MINUTES_BEFORE))
                                        onEvent(EventDetailOnClick.ToggleReminderMenu)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(id = R.string.ThirtyMinBefore))
                                    },
                                    onClick = {
                                        onEvent(EventDetailOnClick.ReminderTypeChanged(ReminderType.THIRTY_MINUTES_BEFORE))
                                        onEvent(EventDetailOnClick.ToggleReminderMenu)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(id = R.string.OneHourBefore))
                                    },
                                    onClick = {
                                        onEvent(EventDetailOnClick.ReminderTypeChanged(ReminderType.ONE_HOUR_BEFORE))
                                        onEvent(EventDetailOnClick.ToggleReminderMenu)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(id = R.string.SixHoursBefore))
                                    },
                                    onClick = {
                                        onEvent(EventDetailOnClick.ReminderTypeChanged(ReminderType.SIX_HOURS_BEFORE))
                                        onEvent(EventDetailOnClick.ToggleReminderMenu)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(id = R.string.OneDayBefore))
                                    },
                                    onClick = {
                                        onEvent(EventDetailOnClick.ReminderTypeChanged(ReminderType.ONE_DAY_BEFORE))
                                        onEvent(EventDetailOnClick.ToggleReminderMenu)
                                    }
                                )
                            }

                            if (state.isInEditMode) {
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
                            modifier = Modifier.padding(top = 20.dp, start = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.Visitors),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = interFont
                            )
                            if (state.isInEditMode && state.isUserEventCreator) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .background(Light2)
                                        .clickable {
                                            onEvent(EventDetailOnClick.ToggleAddingAttendeeDialog)
                                        },
                                    tint = Gray
                                )
                            }
                        }
                    }
                }
                item {
                    if(attendeeList.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                SelectableChipOptions.values().forEachIndexed { index, chip ->
                                    FilterChip(
                                        selected = state.selectedChipIndex == index,
                                        text = when(chip) {
                                            SelectableChipOptions.ALL -> stringResource(id = R.string.All)
                                            SelectableChipOptions.GOING -> stringResource(id = R.string.Going)
                                            SelectableChipOptions.NOT_GOING -> stringResource(id = R.string.NotGoing)
                                        },
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            onEvent(EventDetailOnClick.SelectFilterOption(chip, index))
                                        }
                                        )
                                    }
                                }

                            when(state.selectedChip) {
                                SelectableChipOptions.ALL -> {
                                        Text(
                                            text = stringResource(id = R.string.Going),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = interFont,
                                            color = DarkGray,
                                            modifier = Modifier.padding(start = 12.dp, top = 15.dp)
                                        )
                                    attendeeList.filter { it.isGoing }.forEach {
                                        AttendeeItem(
                                            initials = getInitials(it.fullName),
                                            fullName = it.fullName,
                                            eventCreator = it.userId == state.eventCreatorId,
                                            isInEditMode = state.isInEditMode && state.isUserEventCreator,
                                            onDeleteClick = {
                                                onEvent(EventDetailOnClick.RemoveAttendee(it))
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                        Text(
                                            text = stringResource(id = R.string.NotGoing),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = interFont,
                                            color = DarkGray,
                                            modifier = Modifier.padding(start = 12.dp, top = 15.dp)
                                        )

                                    attendeeList.filter { !it.isGoing }.forEach {
                                        AttendeeItem(
                                            initials = getInitials(it.fullName),
                                            fullName = it.fullName,
                                            eventCreator = it.userId == state.eventCreatorId,
                                            isInEditMode = state.isInEditMode && state.isUserEventCreator,
                                            onDeleteClick = {
                                                onEvent(EventDetailOnClick.RemoveAttendee(it))
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                                SelectableChipOptions.GOING -> {

                                        Text(
                                            text = stringResource(id = R.string.Going),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = interFont,
                                            color = DarkGray,
                                            modifier = Modifier.padding(start = 12.dp, top = 15.dp)
                                        )

                                    attendeeList.filter { it.isGoing }.forEach {
                                        AttendeeItem(
                                            initials = getInitials(it.fullName),
                                            fullName = it.fullName,
                                            eventCreator = it.userId == state.eventCreatorId,
                                            isInEditMode = state.isInEditMode && state.isUserEventCreator,
                                            onDeleteClick = {
                                                onEvent(EventDetailOnClick.RemoveAttendee(it))
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                                SelectableChipOptions.NOT_GOING -> {
                                        Text(
                                            text = stringResource(id = R.string.NotGoing),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = interFont,
                                            color = DarkGray,
                                            modifier = Modifier.padding(start = 12.dp, top = 15.dp)
                                        )

                                    attendeeList.filter { !it.isGoing}.forEach {
                                        AttendeeItem(
                                            initials = getInitials(it.fullName),
                                            fullName = it.fullName,
                                            eventCreator = it.userId == state.eventCreatorId,
                                            isInEditMode = state.isInEditMode && state.isUserEventCreator,
                                            onDeleteClick = {
                                                onEvent(EventDetailOnClick.RemoveAttendee(it))
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(50.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 10.dp)
                            .applyIf(
                                condition = state.isInEditMode,
                                modifier = Modifier.clickable {
                                    if(state.isUserEventCreator) {
                                        onEvent(EventDetailOnClick.DeleteLeaveOrJoinEvent(EventOptions.DELETE))
                                    } else {
                                        if(state.isCurrentUserGoing) {
                                            onEvent(EventDetailOnClick.DeleteLeaveOrJoinEvent(EventOptions.LEAVE))
                                        } else {
                                            onEvent(EventDetailOnClick.DeleteLeaveOrJoinEvent(EventOptions.JOIN))
                                        }
                                    }
                                }
                            )
                    ) {
                        Text(
                            text = if(state.isUserEventCreator) stringResource(id = R.string.DELETE_EVENT) else
                                if(state.isCurrentUserGoing) stringResource(id = R.string.LEAVE_EVENT) else stringResource(id = R.string.JOIN_EVENT),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = interFont,
                            color = Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
    DatePickerDialog(
        initialDate = state.fromDate,
        dialogState = dateDialogState,
        onClick = {
            when(state.dateTimePicker) {
                DateTimeDialogType.FROM_DATE -> onEvent(EventDetailOnClick.FromDateChanged(it))
                DateTimeDialogType.TO_DATE -> onEvent(EventDetailOnClick.ToDateChanged(it))
                DateTimeDialogType.FROM_TIME -> Unit
                DateTimeDialogType.TO_TIME -> Unit
            }
        }
    )
    TimePickerDialog(
        initialTime = state.fromTime,
        dialogState = timeDialogState,
        onClick = {
            when(state.dateTimePicker) {
                DateTimeDialogType.FROM_DATE -> Unit
                DateTimeDialogType.TO_DATE -> Unit
                DateTimeDialogType.FROM_TIME -> onEvent(EventDetailOnClick.FromTimeChanged(it))
                DateTimeDialogType.TO_TIME -> onEvent(EventDetailOnClick.ToTimeChanged(it))
            }
        }
    )
}