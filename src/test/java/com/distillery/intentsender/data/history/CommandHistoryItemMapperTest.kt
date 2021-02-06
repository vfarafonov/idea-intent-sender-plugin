package com.distillery.intentsender.data.history

import com.distillery.intentsender.domain.command.createCommandStub
import com.distillery.intentsender.models.ExtraField
import com.distillery.intentsender.models.IntentFlags
import org.junit.Assert.assertEquals
import org.junit.Test

private val COMMAND_STUB = createCommandStub(
    extras = listOf(
        ExtraField(ExtraField.ExtrasTypes.BOOLEAN, "key_stub_1", "value_stub_1"),
        ExtraField(ExtraField.ExtrasTypes.STRING, "key_stub_2", "value_stub_2"),
    ),
    flags = listOf(
        IntentFlags.FLAG_ACTIVITY_BROUGHT_TO_FRONT,
        IntentFlags.FLAG_ACTIVITY_CLEAR_TOP,
    )
)

class CommandHistoryItemMapperTest {

    private val mapper = CommandHistoryItemMapper()

    @Test
    fun `conversion to history item and back to command returns item which is equals to original`() {
        val historyItem = mapper.mapToHistoryItem(COMMAND_STUB)
        val command = mapper.mapToCommand(historyItem)

        assertEquals(COMMAND_STUB, command)
    }
}
