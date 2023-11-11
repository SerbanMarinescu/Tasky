package com.example.tasky.feature_agenda.presentation.agenda_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.feature_agenda.domain.repository.AgendaRepositories
import com.example.tasky.feature_agenda.domain.use_case.AgendaUseCases
import com.example.tasky.feature_agenda.presentation.util.generateNextDays
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.util.Result
import com.vanpra.composematerialdialogs.MaterialDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val useCases: AgendaUseCases,
    private val repositories: AgendaRepositories,
    private val userPreferences: UserPreferences
): ViewModel() {

    private val _state = MutableStateFlow(AgendaState())
    val state = _state.asStateFlow()

    var dateDialogState by mutableStateOf(MaterialDialogState())

    var username by mutableStateOf("")
        private set

    private val resultChannel = Channel<Result<Unit>>()
    val logoutResult = resultChannel.receiveAsFlow()

    init {
        val user = userPreferences.getAuthenticatedUser()
        user?.let {
            val names = it.fullName.split(" ")
            username = when(names.size) {
                1 -> {
                    "${names[0][0]}${names[0][1]}"
                }

                2 -> {
                    "${names[0][0]}${names[1][0]}"
                }

                else -> ""
            }
        }
        getAgenda()
    }

    fun onEvent(event: AgendaEvent) {

        when(event) {
            is AgendaEvent.Logout -> {
                logout()
            }
            is AgendaEvent.SelectDate -> {
                _state.update {
                    it.copy(
                        currentMonth = event.date.month,
                        daysList = generateNextDays(event.date),
                        currentDate = event.date
                    )
                }
                getAgenda()
            }
            is AgendaEvent.SwipeToRefresh -> {
                getRemoteAgenda()
            }

            is AgendaEvent.SelectDayIndex -> {
                _state.update {
                    it.copy(selectedDayIndex = event.index)
                }
                getAgenda()
            }

            is AgendaEvent.ToggleLogoutBtn -> {
                _state.update {
                    it.copy(
                        showLogoutOption = event.showOption
                    )
                }
            }
        }
    }

    private fun getAgenda() {
        viewModelScope.launch {
            val agendaItems = repositories.agendaRepository.getAgendaForSpecificDay(state.value.currentDate).first()
            _state.update {
                it.copy(itemList = agendaItems)
            }
        }
    }

    private fun getRemoteAgenda() {
        viewModelScope.launch {
            repositories.agendaRepository.fetchAgendaFromRemote(state.value.currentDate)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            val result = useCases.logout()
            resultChannel.send(result)
        }
    }
}