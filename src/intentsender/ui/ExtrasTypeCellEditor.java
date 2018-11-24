package intentsender.ui;

import com.intellij.openapi.ui.ComboBox;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import intentsender.Models.ExtraField;

/**
 * Created by vfarafonov on 31.08.2015.
 */
class ExtrasTypeCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	private ExtraField.ExtrasTypes currentType_;

	@Override
	public Object getCellEditorValue() {
		return currentType_;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value instanceof ExtraField.ExtrasTypes) {
			currentType_ = (ExtraField.ExtrasTypes) value;
		}
		ComboBox comboBox = new ComboBox(ExtraField.ExtrasTypes.values());
		comboBox.setSelectedItem(currentType_);
		comboBox.addActionListener(this);
		return comboBox;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ComboBox comboBox = (ComboBox) e.getSource();
		currentType_ = (ExtraField.ExtrasTypes) comboBox.getSelectedItem();
	}
}
