package ui;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import adb.AdbHelper;

/**
 * Created by vfarafonov on 25.08.2015.
 */
public class MainToolWindow implements ToolWindowFactory {
	private JPanel toolWindowContent;
	private JLabel myLabel;
	private JTable table1;
	private JButton addExtraButton;
	private JTextField actionTextField;
	private JComboBox devicesComboBox;
	private JButton updateDevices;
	private JButton sendIntentButton;
	private ToolWindow mainToolWindow;

	private IDevice[] devices_ = {};

	public MainToolWindow() {
		// Initialize ComboBox
		devicesComboBox.setRenderer(new DevicesListRenderer());
		devicesComboBox.setMaximumRowCount(10);
		// TODO: implement devices auto update
		updateConnectedDevices();

		updateDevices.addActionListener(e -> updateConnectedDevices());
		sendIntentButton.addActionListener(e -> sendIntent());
	}

	/**
	 * Prepares intent parameters and initiates intent sending
	 */
	private void sendIntent() {
		// Check if device is selected
		Object device = devicesComboBox.getSelectedItem();
		if (device == null || !(device instanceof IDevice)){
			return;
		}

		// Prepare and send intent
		String action = actionTextField.getText();
		try {
			AdbHelper.getInstance().sendIntent(action, (IDevice) device);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (AdbCommandRejectedException e) {
			e.printStackTrace();
		} catch (ShellCommandUnresponsiveException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateConnectedDevices() {
		AdbHelper helper = AdbHelper.getInstance();
		devices_ = helper.getDevices();
		if (devices_.length == 0) {
			String[] emptyList = {"Devices not found"};
			devicesComboBox.setModel(new DefaultComboBoxModel<String>(emptyList));
		} else {
			devicesComboBox.setModel(new DefaultComboBoxModel<IDevice>(devices_));
		}
	}

	@Override
	public void createToolWindowContent(Project project, ToolWindow toolWindow) {
		mainToolWindow = toolWindow;
		ContentFactory factory = ContentFactory.SERVICE.getInstance();
		Content content = factory.createContent(toolWindowContent, "", false);
		mainToolWindow.getContentManager().addContent(content);
	}
}
