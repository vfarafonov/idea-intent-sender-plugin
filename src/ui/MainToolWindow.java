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
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import Models.ExtraField;
import adb.AdbHelper;

/**
 * Created by vfarafonov on 25.08.2015.
 */
public class MainToolWindow implements ToolWindowFactory {
	private final ExtrasTableModel tableModel_;
	private JPanel toolWindowContent;
	private JLabel myLabel;
	private JTable extrasTable;
	private JButton addExtraButton;
	private JTextField actionTextField;
	private JComboBox devicesComboBox;
	private JButton updateDevices;
	private JButton sendIntentButton;
	private JTextField dataTextField;
	private JTextField categoryTextField;
	private JTextField mimeTextField;
	private JTextField componentTextField;
	private JTextField flagsTextField;
	private JButton sendStartButton;
	private JScrollPane extasRootLayout;
	private ToolWindow mainToolWindow;

	private IDevice[] devices_ = {};

	public MainToolWindow() {
		// Initialize ComboBox
		devicesComboBox.setRenderer(new DevicesListRenderer());
		devicesComboBox.setMaximumRowCount(10);
		// TODO: implement devices auto update
		updateConnectedDevices();

		updateDevices.addActionListener(e -> updateConnectedDevices());
		sendIntentButton.addActionListener(e -> sendCommand(AdbHelper.CommandType.BROADCAST));
		sendStartButton.addActionListener(e -> sendCommand(AdbHelper.CommandType.START));
		addExtraButton.addActionListener(e -> {
			addExtraLine();
			updateTableVisibility();
		});

		// Set up extras table
		tableModel_ = new ExtrasTableModel();
		extrasTable.setModel(tableModel_);
		extrasTable.setDefaultRenderer(ExtraField.ExtrasTypes.class, new ExtrasTypeCellRenderer());
		extrasTable.setDefaultEditor(ExtraField.ExtrasTypes.class, new ExtrasTypeCellEditor());
		extrasTable.setRowHeight((int) (extrasTable.getRowHeight() * 1.3));
		TableColumn removeColumn = extrasTable.getColumnModel().getColumn(ExtrasTableModel.COLUMNS_COUNT - 1);
		removeColumn.setCellRenderer(new ExtrasDeleteButtonRenderer());
		removeColumn.setCellEditor(new ExtrasDeleteButtonEditor(rowIndex -> {
			tableModel_.removeRow(rowIndex);
			updateTableVisibility();
		}));
	}

	/**
	 * Hides table if it does not have any rows. Shows otherwise
	 */
	private void updateTableVisibility() {
		if (extrasTable.getRowCount() > 0) {
			if (!extasRootLayout.isVisible()) {
				extasRootLayout.setVisible(true);
				extasRootLayout.getParent().revalidate();
				extasRootLayout.getParent().repaint();
			}
		} else {
			if (extasRootLayout.isVisible()) {
				extasRootLayout.setVisible(false);
				extasRootLayout.getParent().revalidate();
				extasRootLayout.getParent().repaint();
			}
		}
	}

	private void addExtraLine() {
		tableModel_.addRow(new ExtraField(ExtraField.ExtrasTypes.STRING, null, null));
	}

	/**
	 * Prepares intent parameters and initiates intent sending
	 */
	private void sendCommand(AdbHelper.CommandType type) {
		// Check if device is selected
		Object device = devicesComboBox.getSelectedItem();
		if (device == null || !(device instanceof IDevice)) {
			return;
		}

		// Prepare and send intent
		String action = actionTextField.getText();
		String data = dataTextField.getText();
		String category = categoryTextField.getText();
		String mime = mimeTextField.getText();
		String component = componentTextField.getText();
		List<ExtraField> extras = tableModel_.getValues();
		try {
			AdbHelper.getInstance().sendCommand(type, (IDevice) device, action, data, category, mime, component, extras);
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
