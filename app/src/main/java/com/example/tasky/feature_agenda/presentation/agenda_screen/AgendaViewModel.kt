package com.example.tasky.feature_agenda.presentation.agenda_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.repository.AgendaRepositories
import com.example.tasky.feature_agenda.domain.use_case.AgendaUseCases
import com.example.tasky.feature_agenda.domain.util.AgendaItemKey
import com.example.tasky.feature_agenda.domain.util.toAgendaItemType
import com.example.tasky.feature_agenda.presentation.util.generateNextDays
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.util.Result
import com.vanpra.composematerialdialogs.MaterialDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val menuVisibilityOptions = mutableMapOf<AgendaItemKey, Boolean>()

    private var debounceTaskJob: Job? = null

    init {
        val user = userPreferences.getAuthenticatedUser()
        user?.let {
            val names = it.fullName.split(Regex("\\s+"))
            username = when(names.size) {
                1 -> {
                    names[0].take(2)
                }

                else -> {
                    val firstNameInitial = names.first().take(1)
                    val lastNameInitial = names.last().take(1)
                    "$firstNameInitial$lastNameInitial"
                }
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
                        isLogoutBtnVisible = !state.value.isLogoutBtnVisible
                    )
                }
            }

            is AgendaEvent.ToggleIsDone -> {
                debounceTaskJob?.cancel()
                val task = event.task
                debounceTaskJob = viewModelScope.launch {
                    delay(500L)
                    useCases.task.updateTask(task.copy(isDone = !task.isDone))
                }
            }

            is AgendaEvent.ToggleItemCreationMenu -> {
                _state.update {
                    it.copy(isItemCreationMenuVisible = !state.value.isItemCreationMenuVisible)
                }
            }

            is AgendaEvent.ToggleIndividualItemMenu -> {
                menuVisibilityOptions[event.itemKey] = event.showIndividualMenu
                _state.update {
                    it.copy(
                        isItemMenuVisible = menuVisibilityOptions
                    )
                }
            }

            is AgendaEvent.DeleteItem -> {
                viewModelScope.launch {
                    when(event.item) {
                        is AgendaItem.Event -> useCases.event.deleteEvent(event.item)
                        is AgendaItem.Reminder -> useCases.reminder.deleteReminder(event.item)
                        is AgendaItem.Task -> useCases.task.deleteTask(event.item)
                    }
                }
            }

            AgendaEvent.ToggleDeletionDialog -> {
                _state.update {
                    it.copy(isDeletionDialogVisible = !state.value.isDeletionDialogVisible)
                }
            }
        }
    }

    private fun getAgenda() {
        viewModelScope.launch {
            repositories.agendaRepository.getAgendaForSpecificDay(state.value.currentDate).collect { agendaItems ->
                agendaItems.forEach {
                    menuVisibilityOptions[AgendaItemKey(it.toAgendaItemType(), it.id)] = false
                }
                _state.update {
                    it.copy(
                        itemList = agendaItems,
                        isItemMenuVisible = menuVisibilityOptions
                    )
                }
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