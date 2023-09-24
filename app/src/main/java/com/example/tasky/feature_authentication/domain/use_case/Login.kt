package com.example.tasky.feature_authentication.domain.use_case

import com.example.tasky.feature_authentication.data.util.AuthResult
import com.example.tasky.feature_authentication.domain.repository.AuthRepository
import com.example.tasky.feature_authentication.domain.util.UseCaseResult
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator

class Login(
    private val repository: AuthRepository,
    private val userDataValidator: UserDataValidator
) {

    suspend operator fun invoke(email: String, password: String): UseCaseResult {

        val emailChecker = userDataValidator.validateEmail(email)
        val passwordChecker = userDataValidator.validatePassword(password)

        if(!emailChecker.isValid) {
            return UseCaseResult.ErrorEmail(emailChecker.emailError
                ?: return UseCaseResult.GenericError("Something went wrong"))
        }

        if(!passwordChecker.isValid) {
            return UseCaseResult.ErrorPassword(passwordChecker.passwordError
                ?: return UseCaseResult.GenericError("Something went wrong"))
        }

        val response = repository.signIn(email, password)

        return when(response) {
            is AuthResult.Authorized -> UseCaseResult.Authorized
            is AuthResult.Error -> UseCaseResult.GenericError(response.message ?: "Something went wrong")
            is AuthResult.Unauthorized -> UseCaseResult.GenericError(response.message ?: "Something went wrong")
        }
    }
}