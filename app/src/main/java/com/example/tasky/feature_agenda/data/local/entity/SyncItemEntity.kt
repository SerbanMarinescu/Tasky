package com.example.tasky.feature_agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.feature_agenda.domain.util.OperationType
import com.example.tasky.feature_agenda.domain.util.ReminderType

@Entity(tableName = "SyncItems")
data class SyncItemEntity(
    val itemId: String,
    val itemType: AgendaItemType,
    val operation: OperationType,
    @PrimaryKey
    val tableId: String = ""
) {
    class Converters {

        @TypeConverter
        fun stringToAgendaItemType(value: String): AgendaItemType {
            return enumValueOf(value)
        }

        @TypeConverter
        fun agendaItemTypeToString(value: AgendaItemType): String {
            return value.name
        }

        @TypeConverter
        fun stringToOperationType(value: String): OperationType {
            return enumValueOf(value)
        }

        @TypeConverter
        fun operationTypeToString(value: OperationType): String {
            return value.name
        }

        @TypeConverter
        fun reminderTypeToString(value: ReminderType): String {
            return value.name
        }

        @TypeConverter
        fun stringToReminderType(value: String): ReminderType {
            return enumValueOf(value)
        }
    }
}
