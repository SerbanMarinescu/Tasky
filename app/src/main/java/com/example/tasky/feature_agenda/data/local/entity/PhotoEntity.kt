package com.example.tasky.feature_agenda.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Photo",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = arrayOf("eventId"),
            childColumns = arrayOf("eventId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class PhotoEntity(
    val key: String,
    val uri: String,
    val eventId: String,
    @PrimaryKey(autoGenerate = true)
    val photoId: Int = 0
)
