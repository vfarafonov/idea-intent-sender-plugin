package ui;

import com.android.ddmlib.IDevice;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * Created by vfarafonov on 26.08.2015.
 */
public class DevicesListRenderer extends BasicComboBoxRenderer {
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
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
		return this;
	}
}
