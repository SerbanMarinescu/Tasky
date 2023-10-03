package com.example.tasky.feature_agenda.data.local.relation

import androidx.room.Entity

@Entity(primaryKeys = ["eventId","attendeeId"])
data class EventAttendeeCrossRef(
    val eventId: Int,
    val attendeeId: Int
)
