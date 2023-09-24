package com.example.tasky.feature_authentication.domain.use_case

import com.example.tasky.feature_authentication.data.util.AuthResult
import com.example.tasky.feature_authentication.domain.repository.AuthRepository
import com.example.tasky.feature_authentication.domain.util.UseCaseResult
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator

class Register(
    private val repository: AuthRepository,
    private val userDataValidator: UserDataValidator
) {
    suspend operator fun invoke(fullName: String, email: String, password: String): UseCaseResult {

        val fullNameChecker = userDataValidator.validateFullName(fullName)
        val emailChecker = userDataValidator.validateEmail(email)
        val passwordChecker = userDataValidator.validatePassword(password)

        if(!fullNameChecker.isValid) {
            return UseCaseResult.ErrorFullName(fullNameChecker.fullNameError
                ?: return UseCaseResult.GenericError("Something went wrong"))
        }

        if(!emailChecker.isValid) {
            return UseCaseResult.ErrorEmail(emailChecker.emailError
                ?: return UseCaseResult.GenericError("Something went wrong"))
        }

        if(!passwordChecker.isValid) {
            return UseCaseResult.ErrorPassword(passwordChecker.passwordError
                ?: return UseCaseResult.GenericError("Something went wrong"))
        }

        val response = repository.signUp(fullName, email, password)

        return when(response) {
            is AuthResult.Authorized -> UseCaseResult.Authorized
            is AuthResult.Error -> UseCaseResult.GenericError(response.message ?: "Something went wrong")
            is AuthResult.Unauthorized -> UseCaseResult.GenericError(response.message ?: "Something went wrong")
        }
    }
}
