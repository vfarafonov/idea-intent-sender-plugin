package com.distillery.intentsender.ui.views

import com.distillery.intentsender.adb.AdbHelper.CommandType
import com.distillery.intentsender.domain.command.Command
import com.distillery.intentsender.models.ExtraField
import com.distillery.intentsender.models.IntentFlags
import com.distillery.intentsender.testutils.parametersOf
import com.distillery.intentsender.ui.views.history.HistoryTextBuilder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class HistoryListCellRendererTest(
    private val command: Command,
    private val expectedText: String,
) {

    companion object {
        @JvmStatic
        @Parameters(name = "Verify mapping of {0}")
        fun data() = parametersOf(
            BROADCAST_COMMAND to BROADCAST_OUTPUT,
            ACTIVITY_COMMAND to ACTIVITY_OUTPUT,
            SERVICE_COMMAND to SERVICE_OUTPUT,
        )
    }

    @Test
    fun `verify text properly created`() {
        val actualText = HistoryTextBuilder.buildText(command)

        assertEquals(expectedText, actualText)
    }

}

private fun String.trimIndentAndRemoveNewLineChars(): String {
    return trimIndent()
        .filter {
            it != '\n'
        }
}

private val BROADCAST_COMMAND = Command(
    action = "stub_action",
    data = "stub_data",
    category = "stub_category",
    mimeType = "stub_mime_type",
    component = "stub_component",
    user = "stub_user",
    extras = emptyList(),
    flags = emptyList(),
    type = CommandType.BROADCAST,
    applicationId = "stub_app_id"
)
private val BROADCAST_OUTPUT = """
    <html>
    Send Broadcast: 
    <br>&nbsp;&nbsp;&nbsp;&nbsp;Action: stub_action
    <br>&nbsp;&nbsp;&nbsp;&nbsp;App id: stub_app_id
    <br>&nbsp;&nbsp;&nbsp;&nbsp;Component: stub_component
    <br>&nbsp;&nbsp;&nbsp;&nbsp;Data: stub_data
    <br>&nbsp;&nbsp;&nbsp;&nbsp;Category: stub_category
    <br>&nbsp;&nbsp;&nbsp;&nbsp;Mime type: stub_mime_type
    <br>&nbsp;&nbsp;&nbsp;&nbsp;No flags
    <br>&nbsp;&nbsp;&nbsp;&nbsp;No extras
    </html>
""".trimIndentAndRemoveNewLineChars()

private val ACTIVITY_COMMAND = Command(
    action = "",
    data = "",
    category = "",
    mimeType = "",
    component = "",
    user = "",
    extras = listOf(ExtraField(ExtraField.ExtrasTypes.STRING, "stub", "stub")),
    flags = listOf(IntentFlags.NONE),
    type = CommandType.START_ACTIVITY,
    applicationId = ""
)
private val ACTIVITY_OUTPUT = """
    <html>
    Start Activity: 
    <br>&nbsp;&nbsp;&nbsp;&nbsp;No flags
    <br>&nbsp;&nbsp;&nbsp;&nbsp;Has extras
    </html>
""".trimIndentAndRemoveNewLineChars()

private val SERVICE_COMMAND = Command(
    action = null,
    data = null,
    category = null,
    mimeType = null,
    component = "this_is_some_very_long_component_name",
    user = null,
    extras = listOf(ExtraField(ExtraField.ExtrasTypes.STRING, "stub", "stub")),
    flags = listOf(IntentFlags.FLAG_ACTIVITY_CLEAR_TOP),
    type = CommandType.START_SERVICE,
    applicationId = null
)
private val SERVICE_OUTPUT = """
    <html>
    Start Service: 
    <br>&nbsp;&nbsp;&nbsp;&nbsp;Component: …_some_very_long_component_name
    <br>&nbsp;&nbsp;&nbsp;&nbsp;Has flags
    <br>&nbsp;&nbsp;&nbsp;&nbsp;Has extras
    </html>
""".trimIndentAndRemoveNewLineChars()
