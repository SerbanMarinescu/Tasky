package com.example.tasky.feature_agenda.presentation.edit_details_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.presentation.theme.Green
import com.example.tasky.presentation.theme.Light
import com.example.tasky.presentation.theme.interFont

@Composable
fun EditDetailsScreen(
    state: EditDetailsState,
    onEvent: (EditDetailsEvent) -> Unit,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.clickable {
                    navigateBack()
                }
            )
            Text(
                text = state.pageTitle?.asString(context) ?: "",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = interFont
            )
            Text(
                text = stringResource(id = R.string.Save),
                color = Green,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    onEvent(EditDetailsEvent.SaveChangedState)
                }
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            color = Light
        )
        Spacer(modifier = Modifier.height(16.dp))
        BasicTextField(
            value = state.content,
            onValueChange = {
                onEvent(EditDetailsEvent.ContentChanged(it))
            },
            textStyle = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = interFont
            ),
            maxLines = 3
        )
    }
}