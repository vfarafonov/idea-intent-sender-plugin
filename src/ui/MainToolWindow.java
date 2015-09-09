package ui;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.intellij.facet.FacetManager;
import com.intellij.ide.util.TreeJavaClassChooserDialog;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiClass;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.xml.GenericAttributeValue;

import org.jetbrains.android.dom.manifest.Manifest;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.facet.AndroidRootUtil;
import org.jetbrains.android.util.AndroidUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;

import Models.Command;
import Models.ExtraField;
import Models.IntentFlags;
import adb.AdbHelper;
import utils.HistoryUtils;

/**
 * Created by vfarafonov on 25.08.2015.
 */
public class MainToolWindow implements ToolWindowFactory {
	public static final String UNKNOWN_ERROR = "Unknown error";
	public static final int FADEOUT_TIME = 2000;
	public static final String COMMAND_SUCCESS = "Command was successfully sent";
	private final ExtrasTableModel tableModel_;
	private JPanel toolWindowContent;
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
	private JButton startActivityButton;
	private JScrollPane extasRootLayout;
	private JButton locateAdbButton;
	private JPanel sendButtonsPanel;
	private JScrollPane parametersScrollPane;
	private JLabel startingAdbLabel;
	private JButton editFlags;
	private JButton startServiceButton;
	private JButton pickClassButton;
	private JButton historyButton;
	private JTextField userTextField;
	private JCheckBox addUserCheckBox;
	private ToolWindow mainToolWindow;

	private IDevice[] devices_ = {};
	private JBList flagsList_ = new JBList(Arrays.asList(IntentFlags.values()));
	private Project project_;
	private AndroidDebugBridge.IDeviceChangeListener devicesListener_ = new AndroidDebugBridge.IDeviceChangeListener() {
		@Override
		public void deviceConnected(IDevice iDevice) {
			SwingUtilities.invokeLater(() -> updateConnectedDevices());
		}

		@Override
		public void deviceDisconnected(IDevice iDevice) {
			SwingUtilities.invokeLater(() -> updateConnectedDevices());
		}

		@Override
		public void deviceChanged(IDevice iDevice, int i) {
			SwingUtilities.invokeLater(() -> updateConnectedDevices());
		}
	};

	public MainToolWindow() {
		flagsList_.setSelectedIndex(0);
		// Initialize ComboBox
		devicesComboBox.setRenderer(new DevicesListRenderer());
		devicesComboBox.setMaximumRowCount(10);
		locateAdbButton.addActionListener(e -> pickAdbLocation());
		String adbLocation = AdbHelper.getAdbLocation();
		if (adbLocation == null) {
			toggleLocateAdbVisibility(true);
		} else {
			hideUi();
			startAdbAndSwitchUI(adbLocation);
		}
		updateDevices.addActionListener(e -> updateConnectedDevices());
		sendIntentButton.addActionListener(e -> sendCommand(AdbHelper.CommandType.BROADCAST));
		startActivityButton.addActionListener(e -> sendCommand(AdbHelper.CommandType.START_ACTIVITY));
		startServiceButton.addActionListener(e -> sendCommand(AdbHelper.CommandType.START_SERVICE));
		pickClassButton.addActionListener(e -> showClassPicker());
		editFlags.addActionListener(e -> showFlagsDialog());
		addExtraButton.addActionListener(e -> {
			addExtraLine();
			updateTableVisibility();
		});
		historyButton.addActionListener(e -> showHistoryDialog());

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

		updateFlagsTextField();
	}

	/**
	 * Shows up history dialog
	 */
	private void showHistoryDialog() {
		JBList commandsList = new JBList(HistoryUtils.getCommandsFromHistory());
		commandsList.setCellRenderer(new HistoryListCellRenderer());
		commandsList.setEmptyText("No data to display");
		commandsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		String[] buttons = {"OK", "Cancel"};
		int result = JOptionPane.showOptionDialog(toolWindowContent, commandsList, "Command history", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
		if (result == 0) {
			updateUiFromCommand((Command) commandsList.getSelectedValue());
		}
	}

	/**
	 * Fills up vies from given command
	 */
	private void updateUiFromCommand(Command command) {
		actionTextField.setText(command.getAction());
		dataTextField.setText(command.getData());
		categoryTextField.setText(command.getCategory());
		mimeTextField.setText(command.getMimeType());
		componentTextField.setText(command.getComponent());
		flagsList_.removeSelectionInterval(0, flagsList_.getItemsCount());
		List<IntentFlags> flags = command.getFlags();
		if (flags != null && flags.size() > 0) {
			for (IntentFlags flag : command.getFlags()) {
				flagsList_.setSelectedValue(flag, false);
			}
		} else {
			flagsList_.setSelectedIndex(0);
		}
		updateFlagsTextField();
		tableModel_.removeAllRows();
		List<ExtraField> extras = command.getExtras();
		if (extras != null && extras.size() > 0) {
			for (ExtraField extra : extras) {
				tableModel_.addRow(extra);
			}
		}
		updateTableVisibility();
	}

	/**
	 * Shows up class picker dialog from current project
	 */
	private void showClassPicker() {
		TreeJavaClassChooserDialog dialog = new TreeJavaClassChooserDialog("Pick up component", project_);
		dialog.setModal(true);
		dialog.show();

		PsiClass selectedClass = dialog.getSelected();

		updateComponent(selectedClass);
	}

	/**
	 * Updates component field from selected class
	 */
	private void updateComponent(PsiClass selectedClass) {
		String androidPackage;
		String fullComponentName;
		if (selectedClass != null) {
			androidPackage = getAndroidPackage(selectedClass);
			fullComponentName = selectedClass.getQualifiedName();
			if (androidPackage != null && !androidPackage.equals("") && fullComponentName != null) {
				int packageIndex = fullComponentName.indexOf(androidPackage);
				if (packageIndex != -1) {
					StringBuilder builder = new StringBuilder(fullComponentName);
					fullComponentName = builder.insert(packageIndex + androidPackage.length(), "/").toString();
				}
				userTextField.setText(androidPackage);
			}
			componentTextField.setText(fullComponentName);
		}
	}

	/**
	 * Gets android app package from selected class
	 *
	 * @return Package or null if package cannot be parsed from sources
	 */
	private String getAndroidPackage(@NotNull PsiClass selectedClass) {
		Module module = ProjectRootManager.getInstance(selectedClass.getProject()).getFileIndex().getModuleForFile(selectedClass.getContainingFile().getVirtualFile());
		if (module == null) {
			return null;
		}
		FacetManager facetManager = FacetManager.getInstance(module);
		AndroidFacet facet = facetManager.getFacetByType(AndroidFacet.ID);
		if (facet == null) {
			return null;
		}
		VirtualFile manifestFile = AndroidRootUtil.getPrimaryManifestFile(facet);
		if (manifestFile == null) {
			return null;
		}
		Manifest manifest = AndroidUtils.loadDomElement(facet.getModule(), manifestFile, Manifest.class);
		if (manifest == null) {
			return null;
		}
		GenericAttributeValue<String> rootPackage = manifest.getPackage();
		return rootPackage.getStringValue();
	}

	/**
	 * Displays dialog with intent flags picking up flow
	 */
	private void showFlagsDialog() {
		int[] oldIndices = flagsList_.getSelectedIndices();
		String[] buttons = {"OK", "Cancel"};
		int result = JOptionPane.showOptionDialog(toolWindowContent, flagsList_, "Select flags", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
		if (result != 0) {
			flagsList_.setSelectedIndices(oldIndices);
		} else {
			if (flagsList_.getSelectedIndices().length > 1 && flagsList_.isSelectedIndex(0)) {
				flagsList_.removeSelectionInterval(0, 0);
			}
			updateFlagsTextField();
		}
	}

	/**
	 * Updates flags text
	 */
	private void updateFlagsTextField() {
		flagsTextField.setText(flagsList_.getSelectedValuesList().toString());
	}

	/**
	 * Displays filepicker and checks if picked file is adb executable
	 */
	private void pickAdbLocation() {
		JFileChooser adbFileChooser = new JFileChooser();
		adbFileChooser.setFileFilter(new FileFilter() {
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
		int returnValue = adbFileChooser.showDialog(toolWindowContent, "Pick");
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = adbFileChooser.getSelectedFile();
			AdbHelper.saveAdbLocation(file.getAbsolutePath());
			startAdbAndSwitchUI(file.getAbsolutePath());
		}
	}

	/**
	 * Checks if adb is connected and switches UI accordingly
	 */
	private void startAdbAndSwitchUI(String adbPath) {
		AdbHelper adbHelper = AdbHelper.getInstance();
		if (adbHelper.initAdb(adbPath, devicesListener_)) {
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

	public void hideUi() {
		devicesComboBox.setVisible(false);
		updateDevices.setVisible(false);
		parametersScrollPane.setVisible(false);
		sendButtonsPanel.setVisible(false);
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
		if (extrasTable.getCellEditor() != null) {
			extrasTable.getCellEditor().stopCellEditing();
		}
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
		String user = null;
		if (addUserCheckBox.isSelected()) {
			String text = userTextField.getText();
			if (text != null && text.length() > 0) {
				user = text;
			}
		}
		List<ExtraField> extras = tableModel_.getValues();
		List<IntentFlags> flags = flagsList_.getSelectedValuesList();
		flags.remove(IntentFlags.NONE);
		Command command = new Command(action, data, category, mime, component, user, extras, flags, type);

		toggleStartButtonsAvailability(false);
		new SwingWorker<String, String>() {
			@Override
			protected String doInBackground() throws Exception {
				String error = null;
				try {
					error = AdbHelper.getInstance().sendCommand(command, (IDevice) device);
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
				handleSendingResult(error, command);
				toggleStartButtonsAvailability(true);
			}
		}.execute();
	}

	private void toggleStartButtonsAvailability(boolean isEnabled) {
		startActivityButton.setEnabled(isEnabled);
		startServiceButton.setEnabled(isEnabled);
		sendIntentButton.setEnabled(isEnabled);
	}

	/**
	 * Shows up appropriate popup
	 *
	 * @param error   Message to display or null if success
	 * @param command Sent command
	 */
	private void handleSendingResult(String error, Command command) {
		JLabel label = new JLabel(COMMAND_SUCCESS);
		label.setForeground(Gray._50);
		BalloonBuilder builder = JBPopupFactory.getInstance().createBalloonBuilder(label);
		builder.setFadeoutTime(FADEOUT_TIME)
				.setShowCallout(false);
		if (error == null) {
			System.out.println("SUCCESS sending command");
			builder.setFillColor(JBColor.BLUE);
			HistoryUtils.saveCommand(command);
		} else {
			System.out.println("Sending command FAILED: " + error);
			label.setText(error);
			label.setForeground(Gray._0);
			builder.setFillColor(JBColor.PINK);
		}
		builder.createBalloon().showInCenterOf(sendButtonsPanel);
	}

	/**
	 * Updates devices list keeping selected device if it is still connected
	 */
	private void updateConnectedDevices() {
		AdbHelper helper = AdbHelper.getInstance();
		String selectedSerial = null;
		Object selectedItem = devicesComboBox.getSelectedItem();
		if (selectedItem != null && selectedItem instanceof IDevice) {
			selectedSerial = ((IDevice) selectedItem).getSerialNumber();
		}
		devices_ = helper.getDevices();
		if (devices_.length == 0) {
			String[] emptyList = {"Devices not found"};
			devicesComboBox.setModel(new DefaultComboBoxModel<String>(emptyList));
			toggleStartButtonsAvailability(false);
		} else {
			devicesComboBox.setModel(new DefaultComboBoxModel<IDevice>(devices_));
			devicesComboBox.setSelectedIndex(findSelectionIndex(selectedSerial));
			toggleStartButtonsAvailability(true);
		}
	}

	/**
	 * Finds device index from list by serial number
	 *
	 * @return Device index in a list. Default value - 0
	 */
	private int findSelectionIndex(String selectedSerial) {
		if (selectedSerial == null) {
			return 0;
		}
		for (int i = 0; i < devices_.length; i++) {
			if (devices_[i].getSerialNumber().equals(selectedSerial)) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public void createToolWindowContent(Project project, ToolWindow toolWindow) {
		project_ = project;
		mainToolWindow = toolWindow;
		ContentFactory factory = ContentFactory.SERVICE.getInstance();
		Content content = factory.createContent(toolWindowContent, "", false);
		mainToolWindow.getContentManager().addContent(content);
	}
}
