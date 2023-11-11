package com.example.tasky.feature_agenda.presentation.agenda_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.feature_agenda.presentation.agenda_screen.AgendaState
import com.example.tasky.presentation.theme.AccountText
import com.example.tasky.presentation.theme.BackgroundAccBubble
import com.example.tasky.presentation.theme.BackgroundBlack
import com.vanpra.composematerialdialogs.MaterialDialogState

@Composable
fun TopSection(
    state: AgendaState,
    dateDialogState: MaterialDialogState,
    username: String,
    showMenuOptions: (Boolean) -> Unit,
    logout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                dateDialogState.show()
            }
        ) {
            Text(
                text = state.currentDate.month.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White

            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(id = R.string.DatePicker),
                tint = Color.White
            )
        }
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(BackgroundAccBubble)
                .clickable {
                    showMenuOptions(true)
                }
        ) {
            Text(
                text = username,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.displayMedium,
                color = AccountText
            )
            DropdownMenu(
                expanded = state.showLogoutOption,
                onDismissRequest = {
                    showMenuOptions(false)
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.Logout),
                            style = MaterialTheme.typography.labelSmall,
                            color = BackgroundBlack
                        )
                    },
                    onClick = {
                        logout()
                    }
                )
            }
        }
    }
}