package com.distillery.intentsender.domain.command

import com.distillery.intentsender.domain.command.CommandParamsValidator.ValidationResult
import com.distillery.intentsender.domain.command.CommandParamsValidator.ValidationResult.Invalid.Error.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandParamsValidatorTest {

    private val validator = CommandParamsValidator()

    @Test
    fun `error returned when application id is set and component is not set`() {
        val command = createCommandStub(
            applicationId = "application_id_stub",
            component = null
        )

        val result = validator.validate(command)

        assertResultIsInvalid(result, COMPONENT_MISSING)
    }

    @Test
    fun `error returned when component is set and application id is not set`() {
        val command = createCommandStub(
            applicationId = null,
            component = "component_stub"
        )

        val result = validator.validate(command)

        assertResultIsInvalid(result, APPLICATION_ID_MISSING)
    }

    @Test
    fun `error returned when component contains application`() {
        val command = createCommandStub(
            component = "appId/component_stub"
        )

        val result = validator.validate(command)

        assertResultIsInvalid(result, COMPONENT_HAS_APPLICATION_ID)
    }

    private fun assertResultIsInvalid(
        result: ValidationResult,
        expectedError: ValidationResult.Invalid.Error
    ) {
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.size == 1)
        assertEquals(expectedError, errors[0])
    }
}
