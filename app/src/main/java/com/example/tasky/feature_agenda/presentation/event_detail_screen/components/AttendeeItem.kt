package com.example.tasky.feature_agenda.presentation.event_detail_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.presentation.theme.DarkGray
import com.example.tasky.presentation.theme.Gray
import com.example.tasky.presentation.theme.Light2
import com.example.tasky.presentation.theme.LightBlue
import com.example.tasky.presentation.theme.interFont

@Composable
fun AttendeeItem(
    initials: String,
    fullName: String,
    eventCreator: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Light2)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Gray)
                ) {
                    Text(
                        text = initials,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = interFont,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = fullName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = interFont,
                    color = DarkGray
                )
            }
            if(eventCreator) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.Creator),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = interFont,
                        color = LightBlue
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = LightBlue
                    )
                }

            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    tint = DarkGray,
                    modifier = Modifier.size(24.dp)
                )
            }

        }
    }
}