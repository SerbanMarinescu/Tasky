package com.example.tasky.feature_agenda.presentation.agenda_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.tasky.R
import com.example.tasky.feature_agenda.presentation.agenda_screen.AgendaState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import java.time.LocalDate

@Composable
fun DatePickerDialog(
    state: AgendaState,
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
            initialDate = state.currentDate.toLocalDate(),
            title = stringResource(id = R.string.DateDialogTitle)
        ) {
            onClick(it)
        }
    }
}