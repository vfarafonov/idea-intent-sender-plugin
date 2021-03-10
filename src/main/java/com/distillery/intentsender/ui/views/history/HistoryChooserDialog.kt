@file:JvmName("HistoryChooserDialog")

package com.distillery.intentsender.ui.views.history

import com.distillery.intentsender.domain.command.Command
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.Component
import java.awt.Dimension
import javax.swing.JOptionPane
import javax.swing.ListSelectionModel

private const val DIALOG_HEIGHT_RATE_IN_PARENT_WINDOW = 0.8

/**
 * Displays dialog which allows user to select one of the given commands.
 *
 * @return Returns selected command or null if dialog was cancelled.
 */
fun show(
    parentComponent: Component,
    commandsHistory: List<Command>,
): Command? {
    val commandsListWidget = createCommandsListWidget(commandsHistory)
    val scrollPane = createScrollPane(commandsListWidget, parentComponent)
    val buttons = arrayOf("OK", "Cancel")
    val result = JOptionPane.showOptionDialog(
        parentComponent,
        scrollPane,
        "Command history",
        JOptionPane.YES_NO_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        buttons,
        buttons.first()
    )
    return if (result == 0 && commandsListWidget.selectedValue != null) {
        commandsListWidget.selectedValue
    } else {
        null
    }
}

private fun createScrollPane(
    commandsListWidget: JBList<Command>,
    parentComponent: Component
): JBScrollPane {
    return JBScrollPane(commandsListWidget).apply {
        val dialogWidth = preferredSize.width
        val dialogHeight = (parentComponent.height * DIALOG_HEIGHT_RATE_IN_PARENT_WINDOW).toInt()
        preferredSize = Dimension(dialogWidth, dialogHeight)
    }
}

private fun createCommandsListWidget(commandsHistory: List<Command>): JBList<Command> {
    return JBList(commandsHistory).apply {
        cellRenderer = HistoryListCellRenderer()
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        setEmptyText("No data to display")
    }
}
