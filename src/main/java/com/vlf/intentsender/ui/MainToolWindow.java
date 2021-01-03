package com.vlf.intentsender.ui;

import com.android.ddmlib.IDevice;
import com.intellij.ide.util.TreeJavaClassChooserDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiClass;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.vlf.intentsender.Models.Command;
import com.vlf.intentsender.Models.ExtraField;
import com.vlf.intentsender.Models.IntentFlags;
import com.vlf.intentsender.adb.AdbHelper;
import com.vlf.intentsender.ui.views.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainToolWindow implements MainToolWindowContract.View {
	public static final String ISSUES_LINK = "https://github.com/WeezLabs/idea-intent-sender-plugin/issues";
	public static final String EMPTY_OUTPUT = "No data to display";
	private static final int FADEOUT_TIME = 2000;
	private static final String COMMAND_SUCCESS = "Command was successfully sent";
	private final ExtrasTableModel tableModel_;
	private final JBList flagsList_ = new JBList(Arrays.asList(IntentFlags.values()));
	private JPanel toolWindowContent;
	private JTable extrasTable;
	private JButton addExtraButton;
	private JComboBox devicesComboBox;
	private JButton updateDevices;
	private JButton sendIntentButton;
	private JTextField dataTextField;
	private JTextField categoryTextField;
	private JTextField mimeTextField;
	private JTextField componentTextField;
	private JTextField flagsTextField;
	private JButton startActivityButton;
	private JScrollPane extrasRootLayout;
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
	private JButton sendFeedbackButton;
	private JButton showTerminalOutputButton;
	private AutoCompleteComboBox actionsComboBox;
	private ToolWindow mainToolWindow;

	private final MainToolWindowContract.Presenter presenter_;

	@SuppressWarnings("unchecked")
	public MainToolWindow(ToolWindow toolWindow, Project project) {
		mainToolWindow = toolWindow;

		presenter_ = new MainToolWindowPresenter(this, project);

		flagsList_.setSelectedIndex(0);
		// Initialize ComboBox
		devicesComboBox.setRenderer(new DevicesListRenderer());
		devicesComboBox.setMaximumRowCount(10);
		devicesComboBox.addItemListener(itemEvent -> {
			if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
				presenter_.onDeviceSelected((IDevice) itemEvent.getItem());
			}
		});
		locateAdbButton.addActionListener(__ -> presenter_.onLocateAdbClicked());
		updateDevices.addActionListener(__ -> presenter_.onUpdateDevicesClicked());
		sendIntentButton.addActionListener(e -> sendCommand(AdbHelper.CommandType.BROADCAST));
		startActivityButton.addActionListener(e -> sendCommand(AdbHelper.CommandType.START_ACTIVITY));
		startServiceButton.addActionListener(e -> sendCommand(AdbHelper.CommandType.START_SERVICE));
		pickClassButton.addActionListener(__ -> presenter_.onPickComponentClicked());
		editFlags.addActionListener(__ -> showFlagsDialog());
		addExtraButton.addActionListener(__ -> {
			addExtraLine();
			updateTableVisibility();
		});
		historyButton.addActionListener(__ -> presenter_.onShowHistoryClicked());

		// Set up extras table
		tableModel_ = new ExtrasTableModel();
		extrasTable.setModel(tableModel_);
		extrasTable.setDefaultRenderer(ExtraField.ExtrasTypes.class, new ExtrasTypeCellRenderer());
		extrasTable.setDefaultEditor(ExtraField.ExtrasTypes.class, new ExtrasTypeCellEditor());
		extrasTable.setRowHeight((int) (extrasTable.getRowHeight() * 1.3));
		TableColumn removeColumn = extrasTable.getColumnModel().getColumn(ExtrasTableModel.COLUMNS_COUNT - 1);
		removeColumn.setCellRenderer(new ExtrasDeleteButtonRenderer());
		removeColumn.setCellEditor(new ExtrasDeleteButtonEditor(new ExtrasDeleteButtonEditor.RemoveRowListener() {
			@Override
			public void onRowRemoved(int rowIndex) {
				tableModel_.removeRow(rowIndex);
				updateTableVisibility();
			}
		}));

		sendFeedbackButton.setBorderPainted(false);
		sendFeedbackButton.setOpaque(false);
		sendFeedbackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter_.onSendFeedbackClicked();
			}
		});
		updateFlagsTextField();
		showTerminalOutputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter_.onShowTerminalOutputClicked();
			}
		});

		presenter_.onViewStart();
	}

	JPanel getContent() {
		return toolWindowContent;
	}

	@Override
	public void showClassPicker(@NotNull Project project) {
		TreeJavaClassChooserDialog dialog = new TreeJavaClassChooserDialog("Pick up component", project);
		dialog.setModal(true);
		dialog.show();

		PsiClass selectedClass = dialog.getSelected();

		presenter_.onComponentSelected(selectedClass);
	}

	@Override
	public void setUser(@NotNull String user) {
		userTextField.setText(user);
	}

	@Override
	public void setComponent(@Nullable String fullComponentName) {
		componentTextField.setText(fullComponentName);
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
		flagsTextField.setText(Arrays.toString(flagsList_.getSelectedValues()));
	}

	@Override
	public void setLocateAdbButtonVisible(boolean isVisible) {
		locateAdbButton.setVisible(isVisible);
		locateAdbButton.getParent().invalidate();
		locateAdbButton.getParent().validate();
		locateAdbButton.getParent().repaint();
	}

	@Override
	public void setIntentCreationLayoutVisible(boolean isVisible) {
		devicesComboBox.setVisible(isVisible);
		updateDevices.setVisible(isVisible);
		parametersScrollPane.setVisible(isVisible);
		sendButtonsPanel.setVisible(isVisible);
	}

	/**
	 * Hides table if it does not have any rows. Shows otherwise
	 */
	private void updateTableVisibility() {
		if (extrasTable.getRowCount() > 0) {
			if (!extrasRootLayout.isVisible()) {
				extrasRootLayout.setVisible(true);
				extrasRootLayout.getParent().invalidate();
				extrasRootLayout.getParent().validate();
				extrasRootLayout.getParent().repaint();
			}
		} else {
			if (extrasRootLayout.isVisible()) {
				extrasRootLayout.setVisible(false);
				extrasRootLayout.getParent().invalidate();
				extrasRootLayout.getParent().validate();
				extrasRootLayout.getParent().repaint();
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

		// Prepare and send intent
		String user = null;
		if (addUserCheckBox.isSelected()) {
			String text = userTextField.getText();
			if (text != null && text.length() > 0) {
				user = text;
			}
		}
		List<IntentFlags> flags = new ArrayList<IntentFlags>();
		for (Object flag : flagsList_.getSelectedValues()) {
			if (flag instanceof IntentFlags) {
				flags.add((IntentFlags) flag);
			}
		}
		flags.remove(IntentFlags.NONE);
		final Command command = new Command(
				actionsComboBox.getText(),
				dataTextField.getText(),
				categoryTextField.getText(),
				mimeTextField.getText(),
				componentTextField.getText(),
				user,
				tableModel_.getValues(),
				flags,
				type
		);
		presenter_.onSendCommandClicked(command);
	}

	@Override
	public void showCommandSentSuccessfully() {
		JBColor fillColor = new JBColor(10930928, 10930928);
		showCommandExecutionResultBalloon(COMMAND_SUCCESS, Gray._50, fillColor);
	}

	@Override
	public void showCommandExecutionError(@NotNull String errorText) {
		showCommandExecutionResultBalloon(errorText, Gray._0, JBColor.PINK);
	}

	private void showCommandExecutionResultBalloon(
			@NotNull String message,
			@NotNull Color labelForeground,
			@NotNull JBColor fillColor) {
		JLabel label = new JLabel(message);
		label.setForeground(labelForeground);
		JBPopupFactory.getInstance().createBalloonBuilder(label)
				.setFadeoutTime(FADEOUT_TIME)
				.setShowCallout(false)
				.setFillColor(fillColor)
				.createBalloon()
				.showInCenterOf(sendButtonsPanel);
	}

	@Override
	public void setAdbInitProgressIndicatorVisible(boolean isVisible) {
		startingAdbLabel.setVisible(isVisible);
		startingAdbLabel.getParent().invalidate();
		startingAdbLabel.getParent().validate();
		startingAdbLabel.getParent().repaint();
	}

	@Override
	public void showNoDevicesConnected() {
		String[] listWithEmptyItemNote = new String[]{"Devices not found"};
		devicesComboBox.setModel(new DefaultComboBoxModel<>(listWithEmptyItemNote));
	}

	@Override
	public void showAvailableDevices(@NotNull IDevice[] devices, @Nullable IDevice selectedDevice) {
		devicesComboBox.setModel(new DefaultComboBoxModel<>(devices));

		int selectedIndex = ArrayUtils.indexOf(devices, selectedDevice);
		devicesComboBox.setSelectedIndex(selectedIndex);
	}

	@Override
	public void pickAdbLocation() {
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
				return nameWithoutExtension.equalsIgnoreCase("adb");
			}

			@Override
			public String getDescription() {
				return "Adb executable";
			}
		});
		int returnValue = adbFileChooser.showDialog(toolWindowContent, "Pick");
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = adbFileChooser.getSelectedFile();
			presenter_.onAdbLocationPicked(file);
		}
	}

	@Override
	public void enableStartButtons(boolean isEnabled) {
		startActivityButton.setEnabled(isEnabled);
		startServiceButton.setEnabled(isEnabled);
		sendIntentButton.setEnabled(isEnabled);
	}

	@Override
	public void showCommandsFromHistoryChooser(@NotNull List<? extends Command> commandsHistory) {
		JBList commandsList = new JBList(commandsHistory);
		commandsList.setCellRenderer(new HistoryListCellRenderer());
		commandsList.setEmptyText("No data to display");
		commandsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		String[] buttons = {"OK", "Cancel"};
		int result = JOptionPane.showOptionDialog(
				toolWindowContent,
				commandsList,
				"Command history",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE,
				null,
				buttons,
				buttons[0]
		);
		if (result == 0) {
			presenter_.onCommandSelectedFromHistory((Command) commandsList.getSelectedValue());
		}
	}

	@Override
	public void updateUiFromCommand(@NotNull Command command) {
		actionsComboBox.setText(command.getAction());
		dataTextField.setText(command.getData());
		categoryTextField.setText(command.getCategory());
		mimeTextField.setText(command.getMimeType());
		componentTextField.setText(command.getComponent());
		userTextField.setText(command.getUser());
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

	@Override
	public void showTerminalOutput(@Nullable String lastCommandOutput) {
		String displayText = lastCommandOutput != null ? lastCommandOutput : EMPTY_OUTPUT;
		JTextArea textArea = new JTextArea(displayText, 15, 0);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		JBScrollPane scrollPane = new JBScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(toolWindowContent.getWidth(), (int) (toolWindowContent.getHeight() * 0.5f)));
		JOptionPane.showMessageDialog(toolWindowContent, scrollPane, "Last command output", JOptionPane.PLAIN_MESSAGE);
	}
}
