package com.example.tasky.feature_agenda.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.tasky.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import java.time.LocalDate

@Composable
fun DatePickerDialog(
    initialDate: LocalDate,
    dialogState: MaterialDialogState,
    onClick: (LocalDate) -> Unit
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
        datepicker(
            initialDate = initialDate,
            title = stringResource(id = R.string.DateDialogTitle)
        ) {
            onClick(it)
        }
    }
}