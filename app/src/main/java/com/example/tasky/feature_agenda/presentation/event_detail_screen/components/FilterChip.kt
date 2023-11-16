package com.example.tasky.feature_agenda.presentation.event_detail_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.presentation.theme.BackgroundBlack
import com.example.tasky.presentation.theme.DarkGray
import com.example.tasky.presentation.theme.Light2
import com.example.tasky.presentation.theme.interFont

@Composable
fun FilterChip(
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 100.dp, height = 30.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(if (selected) BackgroundBlack else Light2)
            .clickable {
                onClick()
            }
    ) {
        Text(
            text = text,
            color = if(selected) Color.White else DarkGray,
            fontSize = 14.sp,
            fontFamily = interFont,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}