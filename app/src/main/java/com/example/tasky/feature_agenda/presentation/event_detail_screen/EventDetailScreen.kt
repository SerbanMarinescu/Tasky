package com.example.tasky.feature_agenda.presentation.event_detail_screen

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.tasky.feature_agenda.presentation.event_detail_screen.components.FilterChip
import com.example.tasky.feature_agenda.presentation.util.SelectableChipOptions
import com.example.tasky.feature_agenda.presentation.util.formatDateTimeOfPattern
import com.example.tasky.presentation.theme.BackgroundBlack
import com.example.tasky.presentation.theme.BackgroundWhite
import com.example.tasky.presentation.theme.DarkGray
import com.example.tasky.presentation.theme.Gray
import com.example.tasky.presentation.theme.Light
import com.example.tasky.presentation.theme.Light2
import com.example.tasky.presentation.theme.LightBlue
import com.example.tasky.presentation.theme.LightGreen
import com.example.tasky.presentation.theme.interFont
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventDetailScreen(
    state: EventDetailState
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
                    TODO("Navigate Back")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                text = if(state.editMode) stringResource(id = R.string.EditEvent) else
                    formatDateTimeOfPattern(state.currentDate, "dd MMMM yyyy"),
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
                        TODO("Save event and navigate back")
                    }
                )
            } else {
                IconButton(
                    onClick = {
                        TODO("Toggle edit mode")
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
                        .run {
                            if (state.editMode) {
                                clickable {
                                    TODO("Navigate to Edit Details Screen for Title")
                                }
                            } else {
                                this
                            }
                        }
                    ,
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
                                    TODO("Navigate to Edit Details Screen for Description")
                                }
                            } else {
                                this
                            }
                        }
                    ,
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
                    if (state.editMode) {
                        Icon(
                            imageVector = Icons.Default.ArrowRight,
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                if(state.addingPhotos) {
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
                                state.photoList.forEach {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .border(
                                                3.dp,
                                                color = LightBlue,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                    ) {
//                                        Image(
//                                            painter = painterResource(id = ),
//                                            contentDescription = null,
//                                            contentScale = ContentScale.Crop,
//                                            modifier = Modifier
//                                                .fillMaxSize()
//                                                .clip(RoundedCornerShape(10.dp))
//                                        )
                                        TODO("Set the image from the list and OnClick to navigate to PhotoDetailScreen")
                                    }
                                }
                                if(state.photoList.size < 10) {
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
                                                    TODO("Open device gallery and add photo")
                                                }
                                            ,
                                            tint = Gray
                                        )
                                    }

                                }

                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Light2)
                            .run {
                                if(state.editMode) {
                                    clickable {
                                        TODO("Toggle addingPhotos mode and Open device gallery and add photo")
                                    }
                                } else {
                                    this
                                }
                            }
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
                        .padding(top = 8.dp)
                    ,
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
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                        if(state.editMode) {
                            Icon(
                                imageVector = Icons.Default.ArrowRight,
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.weight(0.5f),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = state.fromDate.format(DateTimeFormatter.ofPattern("MMM d yyyy")),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = interFont
                        )
                        if(state.editMode) {
                            Icon(
                                imageVector = Icons.Default.ArrowRight,
                                contentDescription = null
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
                        .padding(top = 8.dp)
                    ,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                            .padding(start = 17.dp, end = 17.dp)
                        ,
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
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                        if(state.editMode) {
                            Icon(
                                imageVector = Icons.Default.ArrowRight,
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.weight(0.5f),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = state.toDate.format(DateTimeFormatter.ofPattern("MMM d yyyy")),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = interFont
                        )
                        if(state.editMode) {
                            Icon(
                                imageVector = Icons.Default.ArrowRight,
                                contentDescription = null
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
                        .run {
                            if (state.editMode) {
                                clickable {
                                    TODO("Toggle reminder menu")
                                }
                            } else {
                                this
                            }
                        }
                    ,
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
                            fontWeight = FontWeight.Normal,
                            fontFamily = interFont
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
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier
                            .background(Light2)
                            .run {
                                if(state.editMode) {
                                    clickable {
                                        TODO("Open Dialog To Add Attendee")
                                    }
                                } else {
                                    this
                                }
                            }
                        ,
                        tint = Gray
                    )
                }

                if(state.attendees.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(top = 10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                ,
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
                                        onClick = {
                                            TODO("Select the corresponding chip")
                                        }
                                    )
                                }
                            }
                        }
                        when(state.selectedChip) {
                            SelectableChipOptions.ALL -> {
                                item {
                                    Text(
                                        text = stringResource(id = R.string.Going),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = interFont,
                                        color = DarkGray,
                                        modifier = Modifier.padding(start = 12.dp, top = 15.dp)
                                    )
                                }
                                items(state.attendees.filter { TODO("Filter by isGoing") }) {
                                    //AttendeeItem(initials = , fullName = , eventCreator = )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                item {
                                    Text(
                                        text = stringResource(id = R.string.NotGoing),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = interFont,
                                        color = DarkGray,
                                        modifier = Modifier.padding(start = 12.dp, top = 15.dp)
                                    )
                                }
                                items(state.attendees.filter { TODO("Filter by !isGoing") }) {
                                    //AttendeeItem(initials = , fullName = , eventCreator = )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                            SelectableChipOptions.GOING -> {
                                item {
                                    Text(
                                        text = stringResource(id = R.string.Going),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = interFont,
                                        color = DarkGray,
                                        modifier = Modifier.padding(start = 12.dp, top = 15.dp)
                                    )
                                }
                                items(state.attendees.filter { TODO("Filter by isGoing") }) {
                                    //AttendeeItem(initials = , fullName = , eventCreator = )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                            SelectableChipOptions.NOT_GOING -> {
                                item {
                                    Text(
                                        text = stringResource(id = R.string.NotGoing),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = interFont,
                                        color = DarkGray,
                                        modifier = Modifier.padding(start = 12.dp, top = 15.dp)
                                    )
                                }
                                items(state.attendees.filter { TODO("Filter by !isGoing") }) {
                                    //AttendeeItem(initials = , fullName = , eventCreator = )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "DELETE EVENT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = interFont,
                        color = Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    TODO("Select DeleteEvent or Join Event or Leave Event")
                }
            }
        }
    }
}