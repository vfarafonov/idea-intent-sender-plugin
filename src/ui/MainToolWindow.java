package ui;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;

import Models.ExtraField;
import adb.AdbHelper;

/**
 * Created by vfarafonov on 25.08.2015.
 */
public class MainToolWindow implements ToolWindowFactory {
	public static final String UNKNOWN_ERROR = "Unknown error";
	public static final int FADEOUT_TIME = 2000;
	public static final String COMMAND_SUCCESS = "Command was successfully sent";
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
	private JButton locateAdbButton;
	private JPanel sendButtonsPanel;
	private JScrollPane parametersScrollPane;
	private JLabel startingAdbLabel;
	private ToolWindow mainToolWindow;

	private IDevice[] devices_ = {};

	public MainToolWindow() {
		// Initialize ComboBox
		devicesComboBox.setRenderer(new DevicesListRenderer());
		devicesComboBox.setMaximumRowCount(10);
		// TODO: implement devices auto update
		locateAdbButton.addActionListener(e -> pickAdbLocation());
		String adbLocation = AdbHelper.getAdbLocation();
		if (adbLocation == null) {
			toggleLocateAdbVisibility(true);
		} else {
			startAdbAndSwitchUI(adbLocation);
		}
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
	 * Displays filepicker and checks if picked file is adb executable
	 */
	private void pickAdbLocation() {
		System.out.println("pickAdbLocation");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String nameWithoutExtension = f.getName();
				int lastDotIndex = nameWithoutExtension.lastIndexOf(".");
				if (lastDotIndex != -1) {
					nameWithoutExtension = nameWithoutExtension.substring(0, lastDotIndex);
				}
				if (nameWithoutExtension.equalsIgnoreCase("adb")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "Adb executable";
			}
		});
		int returnValue = fileChooser.showDialog(toolWindowContent, "Pick");
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			startAdbAndSwitchUI(file.getAbsolutePath());
		}
	}

	/**
	 * Checks if adb is connected and switches UI accordingly
	 *
	 * @param adbPath
	 */
	private void startAdbAndSwitchUI(String adbPath) {
		AdbHelper adbHelper = AdbHelper.getInstance();
		if (adbHelper.initAdb(adbPath)) {
			if (!adbHelper.isConnected()) {
				locateAdbButton.setVisible(false);
				startingAdbLabel.setVisible(true);
				startingAdbLabel.getParent().revalidate();
				startingAdbLabel.getParent().repaint();
				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						adbHelper.restartAdb();
						return null;
					}

					@Override
					protected void done() {
						startingAdbLabel.setVisible(false);
						startingAdbLabel.getParent().revalidate();
						startingAdbLabel.getParent().repaint();
						toggleLocateAdbVisibility(false);
						updateConnectedDevices();
					}
				}.execute();
			} else {
				toggleLocateAdbVisibility(false);
				updateConnectedDevices();
			}
		} else {
			toggleLocateAdbVisibility(true);
		}
	}

	private void toggleLocateAdbVisibility(boolean isLocateButtonVisible) {
		locateAdbButton.setVisible(isLocateButtonVisible);
		devicesComboBox.setVisible(!isLocateButtonVisible);
		updateDevices.setVisible(!isLocateButtonVisible);
		parametersScrollPane.setVisible(!isLocateButtonVisible);
		sendButtonsPanel.setVisible(!isLocateButtonVisible);
		locateAdbButton.getParent().revalidate();
		locateAdbButton.getParent().repaint();
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
	 * Prepares intent parameters and sends command in worker thread
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

		sendStartButton.setEnabled(false);
		sendIntentButton.setEnabled(false);
		new SwingWorker<String, String>() {
			@Override
			protected String doInBackground() throws Exception {
				String error = null;
				try {
					error = AdbHelper.getInstance().sendCommand(type, (IDevice) device, action, data, category, mime, component, extras);
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
				return error;
			}

			@Override
			protected void done() {
				String error;
				try {
					error = get();
				} catch (InterruptedException e) {
					error = e.getMessage() != null ? e.getMessage() : UNKNOWN_ERROR;
				} catch (ExecutionException e) {
					error = e.getMessage() != null ? e.getMessage() : UNKNOWN_ERROR;
				}
				handleSendingResult(error);
				sendStartButton.setEnabled(true);
				sendIntentButton.setEnabled(true);
			}
		}.execute();
	}

	/**
	 * Shows up appropriate popup
	 *
	 * @param error Message to display or null if success
	 */
	private void handleSendingResult(String error) {
		JLabel label = new JLabel(COMMAND_SUCCESS);
		label.setForeground(Gray._50);
		BalloonBuilder builder = JBPopupFactory.getInstance().createBalloonBuilder(label);
		builder.setFadeoutTime(FADEOUT_TIME)
				.setShowCallout(false);
		if (error == null) {
			System.out.println("SUCCESS sending command");
			builder.setFillColor(JBColor.BLUE);
		} else {
			System.out.println("Sending command FAILED: " + error);
			label.setText(error);
			label.setForeground(Gray._0);
			builder.setFillColor(JBColor.PINK);
		}
		builder.createBalloon().showInCenterOf(sendButtonsPanel);
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
