package com.distillery.intentsender.utils.documentlisteners

import javax.swing.event.DocumentEvent

/** Notifies given listener when text was updated. */
class TextChangedListener(
    private val onTextChanged: (newText: String) -> Unit,
) : SilentDocumentListener() {

    override fun insertUpdate(event: DocumentEvent) {
        notifyAboutTextChange(event)
    }

    override fun removeUpdate(event: DocumentEvent) {
        notifyAboutTextChange(event)
    }

    override fun changedUpdate(event: DocumentEvent) {
        super.changedUpdate(event)
    }

    private fun notifyAboutTextChange(event: DocumentEvent) {
        val document = event.document
        val text = document.getText(0, document.length)
        onTextChanged(text)
    }
}
