package com.example.tasky.feature_authentication.presentation.register_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.R
import com.example.tasky.feature_authentication.domain.use_case.AuthUseCases
import com.example.tasky.feature_authentication.domain.util.AuthUseCaseResult
import com.example.tasky.feature_authentication.domain.validation.EmailError
import com.example.tasky.feature_authentication.domain.validation.FullNameError
import com.example.tasky.feature_authentication.domain.validation.PasswordError
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator
import com.example.tasky.feature_authentication.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val useCases: AuthUseCases,
    private val userDataValidator: UserDataValidator
): ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val fullNameErrorChannel = Channel<UiText>()
    val fullNameResult = fullNameErrorChannel.receiveAsFlow()

    private val emailErrorChannel = Channel<UiText>()
    val emailResult = emailErrorChannel.receiveAsFlow()

    private val passwordErrorChannel = Channel<UiText>()
    val passwordResult = passwordErrorChannel.receiveAsFlow()

    private val authChannel = Channel<AuthUseCaseResult>()
    val authResult = authChannel.receiveAsFlow()

    fun onEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.ChangePasswordVisibility -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }
            is RegisterEvent.EmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.email
                    )
                }
                validateEmail(state.value.email)
            }
            is RegisterEvent.FullNameChanged -> {
                _state.update {
                    it.copy(
                        fullName = event.fullName
                    )
                }
                validateFullName(state.value.fullName)
            }
            is RegisterEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.password
                    )
                }
                validatePassword(state.value.password)
            }
            is RegisterEvent.SignUp -> {
                signUp()
            }
        }
    }

    private fun validateFullName(fullName: String) {
        val result = userDataValidator.validateFullName(fullName)

        if(result.isValid) {
            _state.update {
                it.copy(
                    isFullNameValid = true,
                    fullNameError = null
                )
            }
        } else {
            _state.update {
                it.copy(
                    isFullNameValid = false
                )
            }
        }
    }

    private fun validateEmail(email: String) {
        val result = userDataValidator.validateEmail((email))

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

    private fun signUp() {
        viewModelScope.launch {
            val fullNameResult = userDataValidator.validateFullName(state.value.fullName)
            val emailResult = userDataValidator.validateEmail(state.value.email)
            val passwordResult = userDataValidator.validatePassword(state.value.password)

            val hasError = listOf(
                fullNameResult,
                emailResult,
                passwordResult
            ).any { !it.isValid }

            if(hasError) {
                _state.update {
                    it.copy(
                        fullNameError = when(fullNameResult.fullNameError) {
                            FullNameError.NAME_EMPTY -> {
                                fullNameErrorChannel.send(UiText.StringResource(R.string.FULL_NAME_EMPTY))
                                "NameEmpty"
                            }
                            FullNameError.NAME_TOO_SHORT -> {
                                fullNameErrorChannel.send(UiText.StringResource(R.string.FULL_NAME_TOO_SHORT))
                                "NameTooShort"
                            }
                            FullNameError.NAME_TOO_LONG -> {
                                fullNameErrorChannel.send(UiText.StringResource(R.string.FULL_NAME_TOO_LONG))
                                "NameTooLong"
                            }
                            null -> null
                        },
                        emailError = when(emailResult.emailError) {
                            EmailError.EMAIL_EMPTY -> {
                                emailErrorChannel.send(UiText.StringResource(R.string.EMAIL_EMPTY))
                                "EmailEmpty"
                            }
                            EmailError.EMAIL_INVALID -> {
                                emailErrorChannel.send(UiText.StringResource(R.string.EMAIL_INVALID))
                                "EmailInvalid"
                            }
                            null -> null
                        },
                        passwordError = when(passwordResult.passwordError) {
                            PasswordError.PASSWORD_EMPTY -> {
                                passwordErrorChannel.send(UiText.StringResource(R.string.PASSWORD_EMPTY))
                                "PasswordEmpty"
                            }
                            PasswordError.PASSWORD_INVALID -> {
                                passwordErrorChannel.send(UiText.StringResource(R.string.PASSWORD_INVALID))
                                "PasswordInvalid"
                            }
                            PasswordError.PASSWORD_TOO_SHORT -> {
                                passwordErrorChannel.send(UiText.StringResource(R.string.PASSWORD_TOO_SHORT))
                                "PasswordTooShort"
                            }
                            null -> null
                        }
                    )
                }
                return@launch
            }

            val authResult = useCases.register(
                state.value.fullName,
                state.value.email,
                state.value.password
            )

            authChannel.send(authResult)
        }
    }
}