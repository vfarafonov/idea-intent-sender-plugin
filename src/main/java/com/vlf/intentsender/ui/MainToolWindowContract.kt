package com.vlf.intentsender.ui

import com.android.ddmlib.IDevice
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
    }

    interface Presenter {

        fun onLocateAdbClicked()

        fun onAdbLocationPicked(adbFile: File)

        fun updateConnectedDevices()

        fun onViewStart()

        fun onDeviceSelected(device: IDevice)
    }
}
