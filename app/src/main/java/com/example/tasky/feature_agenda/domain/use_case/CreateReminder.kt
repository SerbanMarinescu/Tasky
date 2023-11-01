package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.local.entity.SyncItemEntity
import com.example.tasky.feature_agenda.data.util.OperationType
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.ReminderRepository
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.util.ErrorType.HTTP
import com.example.tasky.util.ErrorType.IO
import com.example.tasky.util.ErrorType.OTHER
import com.example.tasky.util.Resource
import com.example.tasky.util.Result
import com.squareup.moshi.Moshi

class CreateReminder(
    private val repository: ReminderRepository,
    private val moshi: Moshi,
    private val db: AgendaDatabase
) {

    suspend operator fun invoke(reminder: AgendaItem.Reminder): Result<Unit> {
        val result = repository.createReminder(reminder)

       return when (result) {
            is Resource.Error -> {
                when (result.errorType) {
                    HTTP -> Result.Error(result.message ?: "Unknown Error")

                    IO -> {
                        val jsonReminder = moshi.adapter(AgendaItem.Reminder::class.java).toJson(reminder)

                        val itemToBeSynced = SyncItemEntity(
                            agendaItem = jsonReminder,
                            itemType = AgendaItemType.REMINDER,
                            operation = OperationType.CREATE
                        )

                        db.agendaDao.upsertItemToBeSynced(itemToBeSynced)

                        Result.Success()
                    }

                    OTHER -> Result.Error(result.message ?: "Unknown Error")

                    null -> Result.Error(result.message ?: "Unknown Error")
                }
            }

            is Resource.Success -> Result.Success()

            else -> Result.Error(result.message ?: "Unknown Error")
        }
    }
}