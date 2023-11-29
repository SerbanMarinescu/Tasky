package com.example.tasky.feature_agenda.presentation.agenda_screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tasky.presentation.theme.BackgroundBlack

@Composable
fun TimeNeedle() {
    BoxWithConstraints(
       modifier = Modifier
           .fillMaxWidth()
           .background(Color.White)
           .height(10.dp)
           .padding(10.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = constraints.maxWidth
            val height = constraints.maxHeight

            drawCircle(
                color = BackgroundBlack,
                radius = 20f,
                center = Offset(0f, height/2f)
            )

            drawLine(
                color = BackgroundBlack,
                start = Offset(0f, height/2f),
                end = Offset(width.toFloat(), height/2f),
                strokeWidth = 7f
            )
        }
    }
}