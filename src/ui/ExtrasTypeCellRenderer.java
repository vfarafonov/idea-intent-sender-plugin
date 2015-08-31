package ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import Models.ExtraField;

/**
 * Created by vfarafonov on 31.08.2015.
 */
public class ExtrasTypeCellRenderer extends DefaultTableCellRenderer {

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
