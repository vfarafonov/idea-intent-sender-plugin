package com.distillery.intentsender.utils.documentlisteners

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Test
import javax.swing.event.DocumentEvent
import javax.swing.text.Document

class TextChangedListenerTest {

    private var text = ""
    private val listener = TextChangedListener { newText -> text = newText }

    @Test
    fun `listener is notified when text part was inserted`() {
        val expectedText = "stub1"
        val documentEventMock = mockDocumentEvent(expectedText)

        listener.insertUpdate(documentEventMock)

        assertEquals(expectedText, text)
    }

    @Test
    fun `listener is notified when text part was removed`() {
        val expectedText = "stub2"
        val documentEventMock = mockDocumentEvent(expectedText)

        listener.removeUpdate(documentEventMock)

        assertEquals(expectedText, text)
    }

    private fun mockDocumentEvent(text: String): DocumentEvent {
        val documentMock = mock<Document>() {
            on { getText(any(), any()) } doReturn text
        }
        return mock {
            on { document } doReturn documentMock
        }
    }
}
