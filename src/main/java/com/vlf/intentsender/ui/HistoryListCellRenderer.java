package com.vlf.intentsender.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.vlf.intentsender.Models.Command;

/**
 * Created by vfarafonov on 08.09.2015.
 */
class HistoryListCellRenderer extends DefaultListCellRenderer {
	private static final int MAX_SUBJECT_LENGTH = 30;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof Command) {
			Command command = (Command) value;
			StringBuilder cellTextBuilder = new StringBuilder();
			String commandSubject;
			switch (command.getType()) {
				case BROADCAST:
					cellTextBuilder.append("Broadcast action: ");
					commandSubject = command.getAction();
					break;
				case START_SERVICE:
					cellTextBuilder.append("Start service: ");
					commandSubject = command.getComponent();
					break;
				case START_ACTIVITY:
					cellTextBuilder.append("Start activity: ");
					commandSubject = command.getComponent();
					break;
				default:
					commandSubject = "";
			}
			if (commandSubject.length() > MAX_SUBJECT_LENGTH) {
				int length = commandSubject.length();
				cellTextBuilder.append("...");
				cellTextBuilder.append(commandSubject.substring(length - MAX_SUBJECT_LENGTH - 1));
			} else {
				cellTextBuilder.append(commandSubject);
			}
			if (command.getExtras() != null && command.getExtras().size() > 0) {
				cellTextBuilder.append(". Has extras");
			}
			if (command.getFlags() != null && command.getFlags().size() > 0) {
				cellTextBuilder.append(". Has some flags");
			}
			setText(cellTextBuilder.toString());
		}
		return this;
	}
}
