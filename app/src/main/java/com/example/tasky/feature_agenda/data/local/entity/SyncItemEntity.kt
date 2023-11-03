package com.example.tasky.feature_agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.feature_agenda.domain.util.OperationType

@Entity(tableName = "SyncItems")
data class SyncItemEntity(
    val itemId: Int,
    val itemType: AgendaItemType,
    val operation: OperationType,
    @PrimaryKey(autoGenerate = true)
    val tableId: Int = 0
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
    }
}
