package com.distillery.intentsender.domain.command

import com.distillery.intentsender.domain.command.CommandParamsValidator.ValidationResult.Invalid
import com.distillery.intentsender.domain.command.CommandParamsValidator.ValidationResult.Valid

/** Validates if [Command]'s fields are properly set and ready to be converted to a reliable command. */
class CommandParamsValidator {

    fun validate(command: Command): ValidationResult {
        val errors = mutableListOf<Invalid.Error>()

        errors.addAll(validateComponentAndApplicationIfSet(command))

        return if (errors.isEmpty()) {
            Valid
        } else {
            Invalid(errors)
        }
    }

    /**
     * Checks that component and application id are both set or both empty.
     *
     * Rationale: result command merges them together in a form of "applicationId/component", like:
     * "com.example.app/com.some.package.some.component.Class"
     */
    private fun validateComponentAndApplicationIfSet(command: Command): List<Invalid.Error> {
        val errors = mutableListOf<Invalid.Error>()
        if (!command.component.isNullOrBlank() && command.applicationId.isNullOrBlank()) {
            errors.add(Invalid.Error.APPLICATION_ID_MISSING)
        }
        if (!command.applicationId.isNullOrBlank() && command.component.isNullOrBlank()) {
            errors.add(Invalid.Error.COMPONENT_MISSING)
        }
        return errors
    }

    sealed class ValidationResult {

        object Valid : ValidationResult()

        class Invalid(val errors: List<Error>) : ValidationResult() {

            enum class Error {
                APPLICATION_ID_MISSING,
                COMPONENT_MISSING,
            }
        }
    }

    companion object Factory {

        fun create() = CommandParamsValidator()
    }
}
