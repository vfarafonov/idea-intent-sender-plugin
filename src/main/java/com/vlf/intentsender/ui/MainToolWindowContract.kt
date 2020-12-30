package com.vlf.intentsender.ui

import com.android.ddmlib.IDevice
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
    }
}
