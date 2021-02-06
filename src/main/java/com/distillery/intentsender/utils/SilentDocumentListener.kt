package com.distillery.intentsender.utils

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/** No op implementation. Allows to override only some of [DocumentListener] callbacks keeping code readable. */
abstract class SilentDocumentListener : DocumentListener {

    override fun insertUpdate(e: DocumentEvent) {
        // No-op
    }

    override fun removeUpdate(e: DocumentEvent) {
        // No-op
    }

    override fun changedUpdate(e: DocumentEvent) {
        // No-op
    }
}
