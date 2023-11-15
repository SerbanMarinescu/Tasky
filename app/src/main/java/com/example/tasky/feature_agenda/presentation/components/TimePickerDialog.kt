package com.example.tasky.feature_agenda.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.tasky.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.time.LocalTime

@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    dialogState: MaterialDialogState,
    onClick: (LocalTime) -> Unit
) {
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                text = stringResource(id = R.string.PositiveBtnText)
            )
            negativeButton(
                text = stringResource(id = R.string.NegativeBtnText)
            )
        }
    ) {
        timepicker(
            initialTime = initialTime,
            title = stringResource(id = R.string.SelectTime)
        ) {
            onClick(it)
        }
    }
}