package com.example.tasky.feature_authentication.domain.use_case

import com.example.tasky.feature_authentication.data.util.AuthResult
import com.example.tasky.feature_authentication.domain.repository.AuthRepository
import com.example.tasky.feature_authentication.domain.util.AuthUseCaseResult
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator

class Register(
    private val repository: AuthRepository,
    private val userDataValidator: UserDataValidator
) {
    suspend operator fun invoke(fullName: String, email: String, password: String): AuthUseCaseResult {

        val fullNameChecker = userDataValidator.validateFullName(fullName)
        val emailChecker = userDataValidator.validateEmail(email)
        val passwordChecker = userDataValidator.validatePassword(password)

        if(!fullNameChecker.isValid) {
            return AuthUseCaseResult.ErrorFullName(fullNameChecker.fullNameError
                ?: return AuthUseCaseResult.GenericError("Something went wrong"))
        }

        if(!emailChecker.isValid) {
            return AuthUseCaseResult.ErrorEmail(emailChecker.emailError
                ?: return AuthUseCaseResult.GenericError("Something went wrong"))
        }

        if(!passwordChecker.isValid) {
            return AuthUseCaseResult.ErrorPassword(passwordChecker.passwordError
                ?: return AuthUseCaseResult.GenericError("Something went wrong"))
        }

        val response = repository.signUp(fullName, email, password)

        return when(response) {
            is AuthResult.Authorized -> AuthUseCaseResult.Success
            is AuthResult.Error -> AuthUseCaseResult.GenericError(response.message ?: "Something went wrong")
            is AuthResult.Unauthorized -> AuthUseCaseResult.GenericError(response.message ?: "Something went wrong")
        }
    }
}
