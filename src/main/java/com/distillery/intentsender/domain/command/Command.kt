package com.distillery.intentsender.domain.command

import com.distillery.intentsender.adb.AdbHelper
import com.distillery.intentsender.models.ExtraField
import com.distillery.intentsender.models.IntentFlags

data class Command(
    val action: String?,
    val data: String?,
    val category: String?,
    val mimeType: String?,
    val component: String?,
    val user: String?,
    val extras: List<ExtraField>,
    val flags: List<IntentFlags>,
    val type: AdbHelper.CommandType,
    val applicationId: String?
)
