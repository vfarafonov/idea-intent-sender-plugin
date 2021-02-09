package com.distillery.intentsender.data.history

import com.distillery.intentsender.adb.AdbHelper
import com.distillery.intentsender.domain.command.Command
import com.distillery.intentsender.models.ExtraField
import com.distillery.intentsender.models.IntentFlags

/**
 * Data model for storing history in project properties.
 *
 * Model is required for backward compatibility.
 */
class CommandHistoryItem(
    val action_: String?,
    val data_: String?,
    val category_: String?,
    val mimeType_: String?,
    val component_: String?,
    val user_: String?,
    val extras_: List<ExtraField>,
    val flags_: List<IntentFlags>,
    val type_: AdbHelper.CommandType,
    val applicationId_: String?
)

class CommandHistoryItemMapper {

    fun mapToHistoryItem(command: Command): CommandHistoryItem {
        return CommandHistoryItem(
            command.action,
            command.data,
            command.category,
            command.mimeType,
            command.component,
            command.user,
            command.extras,
            command.flags,
            command.type,
            command.applicationId,
        )
    }

    fun mapToCommand(commandHistoryItem: CommandHistoryItem): Command {
        return Command(
            commandHistoryItem.action_,
            commandHistoryItem.data_,
            commandHistoryItem.category_,
            commandHistoryItem.mimeType_,
            commandHistoryItem.component_,
            commandHistoryItem.user_,
            commandHistoryItem.extras_,
            commandHistoryItem.flags_,
            commandHistoryItem.type_,
            commandHistoryItem.applicationId_,
        )
    }
}
