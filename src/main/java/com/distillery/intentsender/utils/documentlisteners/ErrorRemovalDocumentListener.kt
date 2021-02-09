package com.distillery.intentsender.utils.documentlisteners

import com.distillery.intentsender.utils.hideError
import javax.swing.JLabel
import javax.swing.event.DocumentEvent

/** Listener for JLabel which removes error by calling [hideError] on any update. */
class ErrorRemovalDocumentListener(
    private val textField: JLabel
) : SilentDocumentListener() {

    override fun insertUpdate(event: DocumentEvent) {
        textField.hideError()
    }

    override fun removeUpdate(event: DocumentEvent) {
        textField.hideError()
    }
}
