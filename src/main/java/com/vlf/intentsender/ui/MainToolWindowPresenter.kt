package com.vlf.intentsender.ui

import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener
import com.android.ddmlib.IDevice
import com.intellij.openapi.project.Project
import com.vlf.intentsender.adb.AdbHelper
import java.io.File
import javax.swing.SwingUtilities
import javax.swing.SwingWorker

class MainToolWindowPresenter(
    private val view: MainToolWindowContract.View,
    private val project: Project
) : MainToolWindowContract.Presenter {

    private val devicesListener: IDeviceChangeListener = DevicesListener()
    private var selectedDeviceSerial: String? = null

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

                updateConnectedDevices()
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
    override fun updateConnectedDevices() {
        val helper = AdbHelper.getInstance()

        val devices = helper.devices
        if (devices.isEmpty()) {
            view.showNoDevicesConnected()
            view.enableStartButtons(false) // toggleStartButtonsAvailability(false)
            selectedDeviceSerial = null
        } else {
            // Find previously selected or use the first device
            val selectedDevice = devices.firstOrNull { it.serialNumber == selectedDeviceSerial }
                ?: devices[0]
            view.showAvailableDevices(devices, selectedDevice)
            view.enableStartButtons(true) // toggleStartButtonsAvailability(true)
        }
    }

    override fun onDeviceSelected(device: IDevice) {
        selectedDeviceSerial = device.serialNumber
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

            updateConnectedDevices()
        }
    }

    private inner class DevicesListener : IDeviceChangeListener {

        override fun deviceConnected(iDevice: IDevice) {
            SwingUtilities.invokeLater { updateConnectedDevices() }
        }

        override fun deviceDisconnected(iDevice: IDevice) {
            SwingUtilities.invokeLater { updateConnectedDevices() }
        }

        override fun deviceChanged(iDevice: IDevice, i: Int) {
            SwingUtilities.invokeLater { updateConnectedDevices() }
        }
    }
}
