package com.distillery.intentsender.utils.documentlisteners

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/** No op implementation. Allows to override only some of [DocumentListener] callbacks keeping code readable. */
abstract class SilentDocumentListener : DocumentListener {

    override fun insertUpdate(event: DocumentEvent) {
        // No-op
    }

    override fun removeUpdate(event: DocumentEvent) {
        // No-op
    }

    override fun changedUpdate(event: DocumentEvent) {
        // No-op
    }
}
