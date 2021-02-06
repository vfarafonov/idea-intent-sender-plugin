package com.distillery.intentsender.ui

import com.android.ddmlib.IDevice
import com.distillery.intentsender.adb.AdbHelper
import com.distillery.intentsender.domain.command.Command
import com.distillery.intentsender.models.ExtraField
import com.distillery.intentsender.models.IntentFlags
import com.distillery.intentsender.domain.command.CommandParamsValidator.ValidationResult
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import java.io.File

interface MainToolWindowContract {

    interface View {

        fun setLocateAdbButtonVisible(isVisible: Boolean)

        fun setIntentCreationLayoutVisible(isVisible: Boolean)

        fun setAdbInitProgressIndicatorVisible(isVisible: Boolean)

        fun showNoDevicesConnected()

        fun showAvailableDevices(devices: Array<IDevice>, selectedDevice: IDevice?)

        fun pickAdbLocation()

        fun enableStartButtons(isEnabled: Boolean)

        fun showCommandsFromHistoryChooser(commandsHistory: List<Command>)

        fun updateUiFromCommand(command: Command)

        fun showTerminalOutput(lastCommandOutput: String?)

        fun showCommandSentSuccessfully()

        fun showCommandExecutionError(errorText: String)

        fun showClassPicker(project: Project)

        fun setUser(user: String)

        fun setComponent(fullComponentName: String?)

        fun showFlagsSelection(allFlags: List<IntentFlags>, selectedFlags: List<IntentFlags>)

        fun displaySelectedFlags(selectedFlags: List<IntentFlags>)

        fun displayParamsErrors(errors: List<ValidationResult.Invalid.Error>)
    }

    interface Presenter {

        fun onLocateAdbClicked()

        fun onAdbLocationPicked(adbFile: File)

        fun onUpdateDevicesClicked()

        fun onViewStart()

        fun onDeviceSelected(device: IDevice)

        fun onShowHistoryClicked()

        fun onCommandSelectedFromHistory(command: Command)

        fun onSendFeedbackClicked()

        fun onShowTerminalOutputClicked()

        fun onSendCommandClicked(
            action: String,
            data: String,
            category: String,
            mimeType: String,
            component: String,
            user: String?,
            extras: List<ExtraField>,
            type: AdbHelper.CommandType,
            applicationId: String?,
        )

        fun onPickComponentClicked()

        fun onComponentSelected(selectedClass: PsiClass?)

        fun onFlagsClicked()

        fun onFlagsSelected(newFlags: List<IntentFlags>)
    }
}
