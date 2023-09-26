package com.example.tasky.feature_authentication.domain.validation

class UserDataValidatorImpl(
    private val emailPatternValidator: EmailPatternValidator
) : UserDataValidator {

    override fun validateFullName(fullName: String): ValidationResult {
        if(fullName.isBlank()) {
            return ValidationResult(
                fullNameError = FullNameError.NAME_EMPTY,
                isValid = false
            )
        }

        if(fullName.length < 4) {
            return ValidationResult(
                fullNameError = FullNameError.NAME_TOO_SHORT,
                isValid = false
            )
        }

        if(fullName.length > 50) {
            return ValidationResult(
                fullNameError = FullNameError.NAME_TOO_LONG,
                isValid = false
            )
        }

        return ValidationResult()
    }

    override fun validateEmail(email: String): ValidationResult {
        if(email.isBlank()) {
            return ValidationResult(
                emailError = EmailError.EMAIL_EMPTY,
                isValid = false
            )
        }

        if(!emailPatternValidator.isValid(email)) {
            return ValidationResult(
                emailError = EmailError.EMAIL_INVALID,
                isValid = false
            )
        }

        return ValidationResult()
    }

    override fun validatePassword(password: String): ValidationResult {
        if(password.isBlank()) {
            return ValidationResult(
                passwordError = PasswordError.PASSWORD_EMPTY,
                isValid = false
            )
        }

        if(!password.contains(Regex("[a-z]")) ||
            !password.contains(Regex("[A-Z]")) ||
            !password.contains(Regex("\\d"))
        ) {
            return ValidationResult(
                passwordError = PasswordError.PASSWORD_INVALID,
                isValid = false
            )
        }

        if(password.length < 9) {
            return ValidationResult(
                passwordError = PasswordError.PASSWORD_TOO_SHORT,
                isValid = false
            )
        }

        return ValidationResult()
    }
}