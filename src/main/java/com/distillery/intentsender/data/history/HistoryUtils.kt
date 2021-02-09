package com.distillery.intentsender.data.history

import com.distillery.intentsender.domain.command.Command
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.ide.util.PropertiesComponent

private const val HISTORY_COUNT = 20
private const val HISTORY_JSON = "HISTORY_JSON"

object HistoryUtils {

    /**
     * Saves command to history list
     */
    fun saveCommand(command: Command) {
        val commandList = mutableListOf<Command>().apply {
            addAll(readCommandsFromProperties())
        }

        while (commandList.size >= HISTORY_COUNT) {
            commandList.removeAt(commandList.size - 1)
        }

        commandList.add(0, command)

        writeCommandsToProperties(commandList)
    }

    private fun writeCommandsToProperties(commandList: List<Command>) {
        val historyItemMapper = CommandHistoryItemMapper()
        val historyItems = commandList.map { historyItemMapper.mapToHistoryItem(it) }
        val historyJson = Gson().toJson(historyItems)
        PropertiesComponent.getInstance().setValue(HISTORY_JSON, historyJson)
    }

    /**
     * Picks commands from history.
     */
    fun getCommandsFromHistory(): List<Command> {
        var commandList = readCommandsFromProperties()

        commandList = extractApplicationIdFromComponentIfNecessary(commandList)

        return commandList
    }

    private fun readCommandsFromProperties(): List<Command> {
        val historyJson = PropertiesComponent.getInstance().getValue(HISTORY_JSON)
        val historyItemMapper = CommandHistoryItemMapper()
        return parseCommands(historyJson)
            .map { historyItemMapper.mapToCommand(it) }
    }

    private fun parseCommands(historyJson: String?): List<CommandHistoryItem> {
        if (historyJson == null) {
            return emptyList()
        }

        return Gson().fromJson(
            historyJson,
            object : TypeToken<List<CommandHistoryItem>>() {}.type
        )
    }

    /**
     * Extracts application id part from component.
     *
     * @see ApplicationIdFromComponentExtractor
     */
    private fun extractApplicationIdFromComponentIfNecessary(commandList: List<Command>): List<Command> {
        val applicationIdExtractor = ApplicationIdFromComponentExtractor()
        return commandList.map { command: Command ->
            applicationIdExtractor.mapCommand(command)
        }
    }
}
