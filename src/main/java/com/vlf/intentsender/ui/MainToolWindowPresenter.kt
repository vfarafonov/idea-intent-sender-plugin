package com.vlf.intentsender.ui

import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener
import com.android.ddmlib.IDevice
import com.intellij.openapi.project.Project
import com.vlf.intentsender.Models.Command
import com.vlf.intentsender.adb.AdbHelper
import com.vlf.intentsender.utils.HistoryUtils
import java.awt.Desktop
import java.io.File
import java.net.URI
import javax.swing.SwingUtilities
import javax.swing.SwingWorker

private const val ERROR_UNKNOWN = "Unknown error"

class MainToolWindowPresenter(
    private val view: MainToolWindowContract.View,
    private val project: Project
) : MainToolWindowContract.Presenter {

    private val devicesListener: IDeviceChangeListener = DevicesListener()
    private var selectedDevice: IDevice? = null
    private var lastCommandOutput: String? = null

    override fun onViewStart() {
        val adbLocation = AdbHelper.getAdbLocation()
        if (adbLocation == null) {
            view.setLocateAdbButtonVisible(true)
            view.setIntentCreationLayoutVisible(false)
        } else {
            view.setLocateAdbButtonVisible(false)
            view.setIntentCreationLayoutVisible(true)

            startAdbAndSwitchUI(adbLocation)
        }
    }

    override fun onAdbLocationPicked(adbFile: File) {
        AdbHelper.saveAdbLocation(adbFile.absolutePath)
        startAdbAndSwitchUI(adbFile.absolutePath)
    }

    /**
     * Checks if adb is connected and switches UI accordingly
     */
    private fun startAdbAndSwitchUI(adbPath: String) {
        val adbHelper = AdbHelper.getInstance()
        if (adbHelper.initAdb(project, adbPath, devicesListener)) {
            if (!adbHelper.isConnected) {
                view.setLocateAdbButtonVisible(false)
                view.setAdbInitProgressIndicatorVisible(true)

                RestartAdbWorker(adbHelper).execute()
            } else {
                view.setLocateAdbButtonVisible(false)
                view.setIntentCreationLayoutVisible(true)

                onUpdateDevicesClicked()
            }
        } else {
            view.setLocateAdbButtonVisible(false)
            view.setIntentCreationLayoutVisible(true)
        }
    }

    override fun onLocateAdbClicked() {
        view.pickAdbLocation()
    }

    /**
     * Updates devices list keeping selected device if it is still connected
     */
    override fun onUpdateDevicesClicked() {
        val helper = AdbHelper.getInstance()

        val devices = helper.devices
        if (devices.isEmpty()) {
            view.showNoDevicesConnected()
            view.enableStartButtons(false)
            selectedDevice = null
        } else {
            // Find previously selected or use the first device
            selectedDevice = devices.firstOrNull { it.serialNumber == selectedDevice?.serialNumber }
                ?: devices[0]
            view.showAvailableDevices(devices, selectedDevice)
            view.enableStartButtons(true)
        }
    }

    override fun onDeviceSelected(device: IDevice) {
        selectedDevice = device
    }

    override fun onShowHistoryClicked() {
        val commandsHistory = HistoryUtils.getCommandsFromHistory()
        view.showCommandsFromHistoryChooser(commandsHistory)
    }

    override fun onCommandSelectedFromHistory(command: Command) {
        view.updateUiFromCommand(command)
    }

    override fun onSendFeedbackClicked() {
        if (!Desktop.isDesktopSupported()) {
            return
        }
        with(Desktop.getDesktop()) {
            if (isSupported(Desktop.Action.BROWSE)) {
                try {
                    browse(URI(MainToolWindow.ISSUES_LINK))
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
            }
        }
    }

    override fun onShowTerminalOutputClicked() {
        view.showTerminalOutput(lastCommandOutput)
    }

    override fun onSendCommandClicked(command: Command) {
        // Check if device is selected
        if (selectedDevice == null) {
            return
        }
        view.enableStartButtons(false)
        SendAdbCommandWorker(command).execute()
    }

    private inner class SendAdbCommandWorker(
        private val command: Command
    ) : SwingWorker<String?, String?>() {
        override fun doInBackground(): String? {
            var error: String? = null
            lastCommandOutput = null
            try {
                AdbHelper.getInstance().setOutputListener { output ->
                    lastCommandOutput = output
                }
                error = AdbHelper.getInstance().sendCommand(command, selectedDevice)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return error
        }

        override fun done() {
            val error: String? = try {
                get()
            } catch (e: Exception) {
                if (e.message != null) e.message else ERROR_UNKNOWN
            }
            if (error == null) {
                handleCommandExecutionSuccess(command)
            } else {
                handleCommandExecutionError(error, command)
            }
            view.enableStartButtons(true)
        }

        private fun handleCommandExecutionError(error: String, command: Command) {
            println("Sending command FAILED: $error")
            view.showCommandExecutionError(error)
        }

        private fun handleCommandExecutionSuccess(command: Command) {
            println("SUCCESS sending command")
            HistoryUtils.saveCommand(command)
            view.showCommandSentSuccessfully()
        }
    }

    private inner class RestartAdbWorker(
        private val adbHelper: AdbHelper
    ) : SwingWorker<Void?, Void?>() {

        override fun doInBackground(): Void? {
            adbHelper.restartAdb()
            return null
        }

        override fun done() {
            // TODO(vfarafonov, 12/23/20): move Done actions to constructor as lambda.
            view.setAdbInitProgressIndicatorVisible(false)
            view.setLocateAdbButtonVisible(false)
            view.setIntentCreationLayoutVisible(true)

            onUpdateDevicesClicked()
        }
    }

    private inner class DevicesListener : IDeviceChangeListener {

        override fun deviceConnected(iDevice: IDevice) {
            SwingUtilities.invokeLater { onUpdateDevicesClicked() }
        }

        override fun deviceDisconnected(iDevice: IDevice) {
            SwingUtilities.invokeLater { onUpdateDevicesClicked() }
        }

        override fun deviceChanged(iDevice: IDevice, i: Int) {
            SwingUtilities.invokeLater { onUpdateDevicesClicked() }
        }
    }
}
