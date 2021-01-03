package com.vlf.intentsender.ui

import com.android.ddmlib.IDevice
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.vlf.intentsender.Models.Command
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

        fun onSendCommandClicked(command: Command)

        fun onPickComponentClicked()

        fun onComponentSelected(selectedClass: PsiClass?)
    }
}
