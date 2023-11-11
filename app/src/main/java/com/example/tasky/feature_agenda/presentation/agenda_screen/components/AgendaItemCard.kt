package com.example.tasky.feature_agenda.presentation.agenda_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.util.AgendaItemKey
import com.example.tasky.feature_agenda.domain.util.toAgendaItemType
import com.example.tasky.presentation.theme.Green
import com.example.tasky.presentation.theme.Light2
import com.example.tasky.presentation.theme.LightGreen

@Composable
fun AgendaItemCard(
    item: AgendaItem,
    timeRange: String,
    onOpenClick: (AgendaItem) -> Unit,
    onEditClick: (AgendaItem) -> Unit,
    onDeleteClick: (AgendaItem) -> Unit,
    isMenuVisible: Boolean,
    onMenuClick: (AgendaItemKey, Boolean) -> Unit,
    selected: Boolean = false,
    toggleIsDone: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                when (item) {
                    is AgendaItem.Event -> LightGreen
                    is AgendaItem.Reminder -> Light2
                    is AgendaItem.Task -> Green
                }
            )
            .clickable {
                onOpenClick(item)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if(selected) Icons.Default.CheckCircleOutline else Icons.Outlined.Circle,
                        contentDescription = null,
                        modifier = Modifier.clickable { toggleIsDone() }
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.displayLarge,
                        textDecoration = if(selected) TextDecoration.LineThrough else TextDecoration.None
                    )
                }
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        onMenuClick(AgendaItemKey(item.toAgendaItemType(), item.id), true)
                    }
                )
                DropdownMenu(
                    expanded = isMenuVisible,
                    onDismissRequest = {
                        onMenuClick(AgendaItemKey(item.toAgendaItemType(), item.id), false)
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.AgendaMenu_Open))
                        },
                        onClick = {
                            onOpenClick(item)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.AgendaMenu_Edit))
                        },
                        onClick = {
                            onEditClick(item)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.AgendaMenu_Delete))
                        },
                        onClick = {
                            onDeleteClick(item)
                        }
                    )
                }
            }
            Text(
                text = item.description ?: "",
                modifier = Modifier
                    .padding(start = 47.dp, top = 16.dp, end = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(45.dp))
        }


        Text(
            text = timeRange,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 8.dp)
        )
    }
}