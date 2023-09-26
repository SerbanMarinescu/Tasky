package com.example.tasky.feature_authentication.domain.use_case

import com.example.tasky.feature_authentication.data.util.AuthResult
import com.example.tasky.feature_authentication.domain.repository.AuthRepository
import com.example.tasky.feature_authentication.domain.util.AuthUseCaseResult
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator

class Login(
    private val repository: AuthRepository,
    private val userDataValidator: UserDataValidator
) {

    suspend operator fun invoke(email: String, password: String): AuthUseCaseResult {

        val emailChecker = userDataValidator.validateEmail(email)
        val passwordChecker = userDataValidator.validatePassword(password)

        if(!emailChecker.isValid) {
            return AuthUseCaseResult.ErrorEmail(emailChecker.emailError
                ?: return AuthUseCaseResult.GenericError("Something went wrong"))
        }

        if(!passwordChecker.isValid) {
            return AuthUseCaseResult.ErrorPassword(passwordChecker.passwordError
                ?: return AuthUseCaseResult.GenericError("Something went wrong"))
        }

        val response = repository.signIn(email, password)

        return when(response) {
            is AuthResult.Authorized -> AuthUseCaseResult.Success
            is AuthResult.Error -> AuthUseCaseResult.GenericError(response.message ?: "Something went wrong")
            is AuthResult.Unauthorized -> AuthUseCaseResult.GenericError(response.message ?: "Something went wrong")
        }
    }
}