package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ValidateAttendee(
    private val userDataValidator: UserDataValidator,
    private val repository: EventRepository
) {
    suspend operator fun invoke(email: String): Flow<Resource<Attendee>> {
        return flow {
            val validationResult = userDataValidator.validateEmail(email)

            if(validationResult.isValid) {
                emit(Resource.Loading())
                val response = repository.doesAttendeeExist(email)
                emit(response)
            } else {
                emit(Resource.Error(message = validationResult.emailError?.name ?: "", errorType = ErrorType.VALIDATION_ERROR))
            }
        }
    }
}