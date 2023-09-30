package com.example.tasky.feature_authentication.presentation.util

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class StringResource(@StringRes val id: Int): UiText()

    fun getString(context: Context): String {
        return when(this) {
            is StringResource -> context.getString(id)
        }
    }
}

