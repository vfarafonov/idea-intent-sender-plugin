package com.distillery.intentsender.domain.command

import com.distillery.intentsender.adb.AdbHelper
import com.distillery.intentsender.models.ExtraField
import com.distillery.intentsender.models.IntentFlags

/** Creates [Command] using stub values if corresponding argument is not supplied. */
fun createCommandStub(
    action: String = "action_stub",
    data: String = "data_stub",
    category: String = "category_stub",
    mimeType: String = "mimeType_stub",
    component: String? = "component_stub",
    user: String = "user_stub",
    extras: List<ExtraField> = emptyList(),
    flags: List<IntentFlags> = emptyList(),
    type: AdbHelper.CommandType = AdbHelper.CommandType.START_ACTIVITY,
    applicationId: String? = "application_id_stub",
): Command {
    return Command(action, data, category, mimeType, component, user, extras, flags, type, applicationId)
}
