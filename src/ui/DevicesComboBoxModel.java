package ui;

import com.android.ddmlib.IDevice;

import javax.swing.DefaultComboBoxModel;

/**
 * Created by vfarafonov on 26.08.2015.
 */
public class DevicesComboBoxModel extends DefaultComboBoxModel<IDevice> {
	public DevicesComboBoxModel(IDevice[] items) {
		super(items);
	}

	@Override
	public Object getSelectedItem() {
		return super.getSelectedItem();
	}
}
