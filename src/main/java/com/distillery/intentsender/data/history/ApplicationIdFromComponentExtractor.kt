package com.distillery.intentsender.data.history

import com.distillery.intentsender.domain.command.Command

private const val APPLICATION_ID_IN_COMPONENT_SEPARATOR = '/'

/**
 * Extracts application id part from component and stores is to corresponding field.
 * <p>
 * Required for plugin update to v0.11. Before 0.11 component entry contained application id. But since v0.11
 * application id is a separate field which is stored separately.
 */
class ApplicationIdFromComponentExtractor {

    fun mapCommand(command: Command): Command {
        if (!command.component.isNullOrBlank()) {
            val separatorIndex = command.component.indexOf(APPLICATION_ID_IN_COMPONENT_SEPARATOR)
            if (separatorIndex != -1) {
                val newApplicationId = if (command.applicationId.isNullOrBlank()) {
                    command.component.substring(0, separatorIndex)
                } else {
                    command.applicationId
                }
                val newComponent = command.component.substring(separatorIndex + 1)

                return command.copy(
                    applicationId = newApplicationId,
                    component = newComponent,
                )
            }
        }

        return command
    }
}
