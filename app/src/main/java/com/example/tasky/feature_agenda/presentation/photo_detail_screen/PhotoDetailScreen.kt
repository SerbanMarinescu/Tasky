package com.example.tasky.feature_agenda.presentation.photo_detail_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.presentation.theme.BackgroundBlack
import com.example.tasky.presentation.theme.interFont

@Preview(showBackground = true)
@Composable
fun PhotoDetailScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
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
                text = stringResource(id = R.string.Photo),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = interFont

            )
            IconButton(
                onClick = {
                    TODO("DeletePhoto")
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
//        Image(
//            painter = ,
//            contentDescription = null,
//            modifier = Modifier.align(Alignment.Center)
//        )
    }
}