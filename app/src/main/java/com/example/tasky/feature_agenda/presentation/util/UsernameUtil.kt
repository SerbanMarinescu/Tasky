package com.example.tasky.feature_agenda.presentation.util

fun getInitials(value: String): String {
    val names = value.split(Regex("\\s+"))

    return when(names.size) {
        1 -> names[0].take(2)
        else -> {
            val firstNameInitial = names.first().take(1)
            val lastNameInitial = names.last().take(1)
            "$firstNameInitial$lastNameInitial"
        }
    }
}