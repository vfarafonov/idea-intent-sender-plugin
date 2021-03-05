package com.distillery.intentsender.ui.views.history

import com.distillery.intentsender.adb.AdbHelper.CommandType.*
import com.distillery.intentsender.domain.command.Command
import com.distillery.intentsender.models.IntentFlags
import java.awt.Component
import javax.swing.BorderFactory
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

private const val MAX_TEXT_LENGTH = 30

class HistoryListCellRenderer : DefaultListCellRenderer() {

    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

        if (value is Command) {
            text = HistoryTextBuilder.buildText(value)
        }

        border = createItemBorder()

        return this
    }

    private fun createItemBorder() = BorderFactory.createCompoundBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 8, 2, 8),
            BorderFactory.createEtchedBorder()
        ),
        BorderFactory.createEmptyBorder(4, 4, 4, 4)
    )
}

object HistoryTextBuilder {

    fun buildText(command: Command): String {
        return buildString {
            append("<html>")
            append(getCommandSubjectText(command))
            append(getActionText(command))
            append(getAppIdText(command))
            append(getComponentText(command))
            append(getDataText(command))
            append(getCategoryText(command))
            append(getMimeTypeText(command))
            append(getFlagsText(command))
            append(getExtrasText(command))
            append("</html>")
        }
    }

    private fun getExtrasText(command: Command): String {
        return if (command.extras.isNotEmpty()) {
            "Has extras".newLineTabbed()
        } else {
            "No extras".newLineTabbed()
        }
    }

    private fun getFlagsText(command: Command): String {
        val hasOnlyNoneFlag = command.flags.size == 1
                && command.flags[0] == IntentFlags.NONE
        return if (command.flags.isEmpty() || hasOnlyNoneFlag) {
            "No flags".newLineTabbed()
        } else {
            "Has flags".newLineTabbed()
        }
    }

    private fun getMimeTypeText(command: Command): String {
        return prepareTextWithPrefixIfNotNullOrReturnEmpty("Mime type", command.mimeType)
    }

    private fun getCategoryText(command: Command): String {
        return prepareTextWithPrefixIfNotNullOrReturnEmpty("Category", command.category)
    }

    private fun getDataText(command: Command): String {
        return prepareTextWithPrefixIfNotNullOrReturnEmpty("Data", command.data)
    }

    private fun getAppIdText(command: Command): String {
        return prepareTextWithPrefixIfNotNullOrReturnEmpty("App id", command.applicationId)
    }

    private fun getComponentText(command: Command): String {
        return prepareTextWithPrefixIfNotNullOrReturnEmpty("Component", command.component)
    }

    private fun getActionText(command: Command): String {
        return prepareTextWithPrefixIfNotNullOrReturnEmpty("Action", command.action)
    }

    /**
     * Adds given prefix with colon to a text if text is not blank. Tabulates the text.
     * If text is null then empty string is returned.
     */
    private fun prepareTextWithPrefixIfNotNullOrReturnEmpty(prefix: String, text: String?): String {
        return if (!text.isNullOrBlank()) {
            "${prefix}: ${text.ellipsize()}".newLineTabbed()
        } else {
            ""
        }
    }

    private fun getCommandSubjectText(command: Command): String {
        return when (command.type) {
            BROADCAST -> "Send Broadcast: "
            START_SERVICE -> "Start Service: "
            START_ACTIVITY -> "Start Activity: "
        }
    }
}

private fun String.newLineTabbed() = "<br>&nbsp;&nbsp;&nbsp;&nbsp;$this"

private fun String.ellipsize(): String {
    return if (length > MAX_TEXT_LENGTH) {
        '\u2026' + this.takeLast(MAX_TEXT_LENGTH)
    } else {
        this
    }
}
