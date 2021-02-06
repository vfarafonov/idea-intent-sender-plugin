package com.distillery.intentsender.utils

import javax.swing.JLabel
import javax.swing.event.DocumentEvent

/** Listener for JLabel which removes error by calling [hideError] on any update. */
class ErrorRemovalDocumentListener(
    private val textField: JLabel
) : SilentDocumentListener() {

    override fun insertUpdate(e: DocumentEvent) {
        textField.hideError()
    }

    override fun removeUpdate(e: DocumentEvent) {
        textField.hideError()
    }
}
