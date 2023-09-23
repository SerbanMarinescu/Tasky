package com.example.tasky.feature_authentication.domain.use_case

import com.example.tasky.feature_authentication.data.util.AuthResult
import com.example.tasky.feature_authentication.domain.repository.AuthRepository
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator

class Register(
    private val repository: AuthRepository,
    private val userDataValidator: UserDataValidator
) {
    suspend operator fun invoke(fullName: String, email: String, password: String): AuthResult {

        if(!userDataValidator.validateFullName(fullName)) {
            return AuthResult.Error(userDataValidator.specifyError())
        }

        if(!userDataValidator.validateEmail(email)) {
            return AuthResult.Error(userDataValidator.specifyError())
        }

        if(!userDataValidator.validatePassword(password)) {
            return AuthResult.Error(userDataValidator.specifyError())
        }

        return repository.signUp(fullName, email, password)

    }
}
