package com.example.tasky.feature_agenda.domain.use_case

import com.example.tasky.feature_agenda.domain.model.Attendee
import com.example.tasky.feature_agenda.domain.repository.EventRepository
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator
import com.example.tasky.util.ErrorType
import com.example.tasky.util.Resource

class ValidateAttendee(
    private val userDataValidator: UserDataValidator,
    private val repository: EventRepository
) {
    suspend operator fun invoke(email: String): Resource<Attendee> {

        val validationResult = userDataValidator.validateEmail(email)

        return if(validationResult.isValid) {
            repository.doesAttendeeExist(email)
        } else {
            Resource.Error(message = validationResult.emailError?.name ?: "", errorType = ErrorType.VALIDATION_ERROR)
        }
    }
}