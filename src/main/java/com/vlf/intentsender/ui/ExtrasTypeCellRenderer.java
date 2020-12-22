package com.vlf.intentsender.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.vlf.intentsender.Models.ExtraField;

/**
 * Created by vfarafonov on 31.08.2015.
 */
class ExtrasTypeCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value instanceof ExtraField.ExtrasTypes) {
			ExtraField.ExtrasTypes type = (ExtraField.ExtrasTypes) value;
			setText(type.toString());
		}
		return this;
	}
}
