package com.example.tasky.feature_agenda.presentation.agenda_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tasky.presentation.theme.DarkGray
import com.example.tasky.presentation.theme.Gray
import com.example.tasky.presentation.theme.SelectedDayColor

@Composable
fun DayChip(
    dayOfWeek: String,
    dayOfMonth: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp, 60.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background( if(selected) SelectedDayColor else Color.White)
            .clickable {
                onClick()
            }

    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayOfWeek,
                style = MaterialTheme.typography.displaySmall,
                color = if(selected) DarkGray else Gray
            )
            Text(
                text = dayOfMonth,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}