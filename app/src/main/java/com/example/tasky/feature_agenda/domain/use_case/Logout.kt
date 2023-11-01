package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.domain.repository.AgendaRepository
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.util.Resource
import com.example.tasky.util.Result

class Logout(
    private val repository: AgendaRepository,
    private val userPrefs: UserPreferences
) {

    suspend operator fun invoke(): Result<Unit> {
        val result = repository.logout()

        return when(result) {
            is Resource.Error -> Result.Error(result.message ?: "Unknown Error")
            is Resource.Success -> {
                userPrefs.clearPreferences()
                Result.Success()
            }
            else -> Result.Error(result.message ?: "Unknown Error")
        }
    }
}