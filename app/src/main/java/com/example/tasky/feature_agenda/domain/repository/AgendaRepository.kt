package com.example.tasky.feature_agenda.domain.repository

import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.util.Resource
import kotlinx.coroutines.flow.Flow

interface AgendaRepository {

    suspend fun getAgenda(fetchFromRemote: Boolean): Flow<Resource<List<AgendaItem>>>
}