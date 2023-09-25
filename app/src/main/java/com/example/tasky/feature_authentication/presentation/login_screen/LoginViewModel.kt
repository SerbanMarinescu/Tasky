package com.example.tasky.feature_authentication.presentation.login_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.feature_authentication.domain.use_case.AuthUseCases
import com.example.tasky.feature_authentication.domain.util.AuthUseCaseResult
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val useCases: AuthUseCases,
    private val userDataValidator: UserDataValidator
) : ViewModel() {
    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    private val resultChannel = Channel<AuthUseCaseResult>()
    val authResult = resultChannel.receiveAsFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    init {
        authenticate()
        _isLoading.value = false
    }

    fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.ChangePasswordVisibility -> {
                _state.value = state.value.copy(
                    isPasswordVisible = !state.value.isPasswordVisible
                )
            }
            is LoginEvent.EmailChanged -> {
                _state.value = state.value.copy(
                    email = event.email
                )
                validateData(state.value.email)
            }
            is LoginEvent.PasswordChanged -> {
                _state.value = state.value.copy(
                    password = event.password
                )
            }
            is LoginEvent.SignIn -> {
                signIn()
            }
        }
    }

    private fun validateData(email: String) {
        val result = userDataValidator.validateEmail(email)

        if(result.isValid) {
            _state.value = state.value.copy(
                isEmailValid = true
            )
        } else {
            _state.value = state.value.copy(
                isEmailValid = false
            )
        }
    }

    private fun signIn() {
        viewModelScope.launch {
           val result = useCases.login(
                state.value.email,
                state.value.password
            )
            resultChannel.send(result)
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            val result = useCases.authenticate()
            resultChannel.send(result)
        }
    }
}