package com.example.tasky.feature_agenda.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tasky.feature_agenda.data.local.entity.AttendeeEntity
import com.example.tasky.feature_agenda.data.local.entity.EventEntity
import com.example.tasky.feature_agenda.data.local.entity.PhotoEntity
import com.example.tasky.feature_agenda.data.local.entity.ReminderEntity
import com.example.tasky.feature_agenda.data.local.entity.SyncItemEntity
import com.example.tasky.feature_agenda.data.local.entity.TaskEntity

@Database(
    entities = [
        AttendeeEntity::class,
        PhotoEntity::class,
        EventEntity::class,
        ReminderEntity::class,
        TaskEntity::class,
        SyncItemEntity::class
    ],
    version = 1
)
@TypeConverters(SyncItemEntity.Converters::class)
abstract class AgendaDatabase: RoomDatabase() {

    abstract val eventDao: EventDao
    abstract val taskDao: TaskDao
    abstract val reminderDao: ReminderDao
    abstract val agendaDao: AgendaDao
}