package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.local.entity.SyncItemEntity
import com.example.tasky.feature_agenda.data.util.OperationType
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import com.example.tasky.util.Result
import com.squareup.moshi.Moshi

class DeleteEvent(
    private val repository: EventRepository,
    private val moshi: Moshi,
    private val db: AgendaDatabase
) {

    suspend operator fun invoke(event: AgendaItem.Event): Result<Unit> {
        val result = repository.deleteEvent(event)

        return when (result) {
            is Resource.Error -> {
                when (result.errorType) {
                    ErrorType.HTTP -> Result.Error(result.message ?: "Unknown Error")

                    ErrorType.IO -> {
                        val jsonEvent = moshi.adapter(AgendaItem.Event::class.java).toJson(event)

                        val itemToBeSynced = SyncItemEntity(
                            agendaItem = jsonEvent,
                            itemType = AgendaItemType.EVENT,
                            operation = OperationType.DELETE
                        )

                        db.agendaDao.upsertItemToBeSynced(itemToBeSynced)
                        Result.Success()
                    }

                    ErrorType.OTHER -> Result.Error(result.message ?: "Unknown Error")

                    null -> Result.Error(result.message ?: "Unknown Error")
                }
            }

            is Resource.Success -> Result.Success()

            else -> Result.Error(result.message ?: "Unknown Error")
        }
    }
}