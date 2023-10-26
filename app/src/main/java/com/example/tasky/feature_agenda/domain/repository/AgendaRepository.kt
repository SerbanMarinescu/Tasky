package com.example.tasky.feature_agenda.domain.repository

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.util.Result
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface AgendaRepository {

    suspend fun logout(): Result<Unit>

    suspend fun getAgendaForSpecificDay(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem?>>

    suspend fun fetchAgendaFromRemote(zonedDateTime: ZonedDateTime): Result<Unit>

    suspend fun syncAgenda(
        deletedEventIds: List<String>,
        deletedReminderIds: List<String>,
        deletedTaskIds: List<String>
    ): Result<Unit>
}