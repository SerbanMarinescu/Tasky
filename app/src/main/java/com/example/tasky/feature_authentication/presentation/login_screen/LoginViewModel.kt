package com.example.tasky.feature_authentication.presentation.login_screen

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.R
import com.example.tasky.feature_authentication.domain.use_case.AuthUseCases
import com.example.tasky.feature_authentication.domain.util.AuthUseCaseResult
import com.example.tasky.feature_authentication.domain.validation.EmailError
import com.example.tasky.feature_authentication.domain.validation.PasswordError
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator
import com.example.tasky.feature_authentication.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val useCases: AuthUseCases,
    private val userDataValidator: UserDataValidator
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val resultChannel = Channel<AuthUseCaseResult>()
    val authResult = resultChannel.receiveAsFlow()

    private val emailErrorChannel = Channel<UiText>()
    val emailErrorResult = emailErrorChannel.receiveAsFlow()

    private val passwordErrorChannel = Channel<UiText>()
    val passwordErrorResult = passwordErrorChannel.receiveAsFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        authenticate()
        _isLoading.value = false
    }

    fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.ChangePasswordVisibility -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }
            is LoginEvent.EmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.email
                    )
                }
                validateEmail(state.value.email)
            }
            is LoginEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.password
                    )
                }
                validatePassword(state.value.password)
            }
            is LoginEvent.SignIn -> {
                signIn()
            }
        }
    }

    private fun validateEmail(email: String) {
        val result = userDataValidator.validateEmail(email)

        if(result.isValid) {
            _state.update {
                it.copy(
                    isEmailValid = true,
                    emailError = null
                )
            }
        } else {
            _state.update {
                it.copy(
                    isEmailValid = false
                )
            }
        }
    }

    private fun validatePassword(password: String) {
        val result = userDataValidator.validatePassword(password)
        if(result.isValid) {
            _state.update {
                it.copy(
                    isPasswordValid = true,
                    passwordError = null
                )
            }
        } else {
            _state.update {
                it.copy(
                    isPasswordValid = false
                )
            }
        }
    }

    private fun signIn() {

        viewModelScope.launch {
            val emailResult = userDataValidator.validateEmail(state.value.email)
            val passwordResult = userDataValidator.validatePassword(state.value.password)

            val hasError = listOf(
                emailResult,
                passwordResult
            ).any{ !it.isValid }

            if(hasError) {
            _state.update {
                it.copy(
                    emailError = when(emailResult.emailError) {
                        EmailError.EMAIL_EMPTY -> {
                            emailErrorChannel.send(UiText.StringResource(R.string.EMAIL_EMPTY))
                            "Email Empty"
                        }
                        EmailError.EMAIL_INVALID -> {
                            emailErrorChannel.send(UiText.StringResource(R.string.EMAIL_INVALID))
                            "Email Invalid"
                        }
                        null -> null
                    },
                    passwordError = when(passwordResult.passwordError) {
                        PasswordError.PASSWORD_EMPTY -> {
                            passwordErrorChannel.send(UiText.StringResource(R.string.PASSWORD_EMPTY))
                            "Password Empty"
                        }
                        PasswordError.PASSWORD_INVALID -> {
                            passwordErrorChannel.send(UiText.StringResource(R.string.PASSWORD_INVALID))
                            "Password Invalid"
                        }
                        PasswordError.PASSWORD_TOO_SHORT -> {
                            passwordErrorChannel.send(UiText.StringResource(R.string.PASSWORD_TOO_SHORT))
                            "Password Too Short"
                        }
                        null -> null
                    }
                )
            }
                return@launch
            }

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