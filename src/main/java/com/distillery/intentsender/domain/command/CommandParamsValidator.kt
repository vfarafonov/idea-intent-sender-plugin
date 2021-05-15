package com.distillery.intentsender.domain.command

import com.distillery.intentsender.domain.command.CommandParamsValidator.ValidationResult.Invalid
import com.distillery.intentsender.domain.command.CommandParamsValidator.ValidationResult.Valid
import com.distillery.intentsender.utils.APPLICATION_ID_IN_COMPONENT_SEPARATOR

/** Validates if [Command]'s fields are properly set and ready to be converted to a reliable command. */
class CommandParamsValidator {

    fun validate(command: Command): ValidationResult {
        val errors = mutableListOf<Invalid.Error>()

        errors.addAll(validateComponent(command))

        return if (errors.isEmpty()) {
            Valid
        } else {
            Invalid(errors)
        }
    }

    /** Validates component info. */
    private fun validateComponent(command: Command): List<Invalid.Error> {
        val errors = mutableListOf<Invalid.Error>()
        if (!command.component.isNullOrBlank()) {
            if (command.applicationId.isNullOrBlank()) {
                // Application Id is expected to be set when Component is set
                errors.add(Invalid.Error.APPLICATION_ID_MISSING)
            }
            if (command.component.contains(APPLICATION_ID_IN_COMPONENT_SEPARATOR)) {
                errors.add(Invalid.Error.COMPONENT_HAS_APPLICATION_ID)
            }
        }
        return errors
    }

    sealed class ValidationResult {

        object Valid : ValidationResult()

        class Invalid(val errors: List<Error>) : ValidationResult() {

            enum class Error {
                APPLICATION_ID_MISSING,
                COMPONENT_HAS_APPLICATION_ID,
            }
        }
    }

    companion object Factory {

        fun create() = CommandParamsValidator()
    }
}
