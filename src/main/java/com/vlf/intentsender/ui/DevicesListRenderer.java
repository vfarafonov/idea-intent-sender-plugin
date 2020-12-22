package com.vlf.intentsender.ui;

import com.android.ddmlib.IDevice;
import com.intellij.ui.ListCellRendererWrapper;

import javax.swing.JList;

/**
 * Created by vfarafonov on 26.08.2015.
 */
class DevicesListRenderer extends ListCellRendererWrapper {
	@Override
	public void customize(JList list, Object value, int index, boolean selected, boolean hasFocus) {
		if (value instanceof IDevice) {
			IDevice device = (IDevice) value;
			String deviceName;
			if (device.isEmulator()) {
				deviceName = device.getAvdName();
			} else {
				deviceName = device.getProperty(IDevice.PROP_DEVICE_MANUFACTURER) + " " + device.getProperty(IDevice.PROP_DEVICE_MODEL);
			}
			setText(deviceName + ": " + device.getSerialNumber());
		}
	}
}
