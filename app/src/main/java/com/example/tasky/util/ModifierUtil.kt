package com.example.tasky.util

import androidx.compose.ui.Modifier

fun Modifier.conditionalModifier(condition: Boolean, modifierType: Modifier): Modifier {
    return if(condition) {
        then(modifierType)
    } else {
        this
    }
}