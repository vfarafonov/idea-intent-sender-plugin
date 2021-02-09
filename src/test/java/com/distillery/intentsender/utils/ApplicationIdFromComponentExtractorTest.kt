package com.distillery.intentsender.utils

import com.distillery.intentsender.data.history.ApplicationIdFromComponentExtractor
import com.distillery.intentsender.domain.command.createCommandStub
import org.junit.Assert.assertEquals
import org.junit.Test

private const val APPLICATION_ID = "some.app.id"
private const val APPLICATION_ID_OTHER = "some.other.app.id"
private const val COMPONENT = "com.some.component"

class ApplicationIdFromComponentExtractorTest {

    private val extractor = ApplicationIdFromComponentExtractor()

    @Test
    fun `application id is properly extracted`() {
        val command = createCommandStub(
            applicationId = null,
            component = "${APPLICATION_ID}/${COMPONENT}"
        )

        val newCommand = extractor.mapCommand(command)

        assertEquals(APPLICATION_ID, newCommand.applicationId)
        assertEquals(COMPONENT, newCommand.component)
    }

    @Test
    fun `application id is not updated but component does not have application id part`() {
        val command = createCommandStub(
            applicationId = APPLICATION_ID_OTHER,
            component = "${APPLICATION_ID}/${COMPONENT}"
        )

        val newCommand = extractor.mapCommand(command)

        assertEquals(APPLICATION_ID_OTHER, newCommand.applicationId)
        assertEquals(COMPONENT, newCommand.component)
    }
}
