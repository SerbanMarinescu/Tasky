package com.example.tasky.feature_agenda.domain.repository

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.util.AgendaResult
import kotlinx.coroutines.flow.Flow

interface AgendaRepository {

    suspend fun logout()

    suspend fun getAgenda(): Flow<List<AgendaItem>>

    suspend fun fetchAgendaFromRemote(): AgendaResult

    suspend fun syncServerWithLocallyDeletedItems(eventIds: List<String>, reminderIds: List<String>, taskIds: List<String>) : AgendaResult

    suspend fun syncLocalCacheWithServer()
}