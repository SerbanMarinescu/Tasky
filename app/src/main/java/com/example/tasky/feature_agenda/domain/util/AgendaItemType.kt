package com.example.tasky.feature_agenda.domain.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AgendaItemType: Parcelable {
    EVENT,
    REMINDER,
    TASK
}