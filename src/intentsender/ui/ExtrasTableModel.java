package intentsender.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import intentsender.Models.ExtraField;

/**
 * Created by vfarafonov on 31.08.2015.
 */
class ExtrasTableModel extends AbstractTableModel {
	public final static int COLUMNS_COUNT = 4;
	private final static String[] COLUMN_NAMES = new String[] {"Type", "Key", "Value", ""};

	private final List<ExtraField> values;

	public ExtrasTableModel() {
		values = new ArrayList<ExtraField>();
	}

	@Override
	public int getRowCount() {
		return values.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMNS_COUNT;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ExtraField field = values.get(rowIndex);
		switch (columnIndex) {
			case 0:
				return field.getType();
			case 1:
				return field.getKey();
			case 2:
				return field.getValue();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex < COLUMNS_COUNT;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		ExtraField field = values.get(rowIndex);
		switch (columnIndex) {
			case 0:
				field.setType((ExtraField.ExtrasTypes) aValue);
				break;
			case 1:
				field.setKey((String) aValue);
				break;
			case 2:
				field.setValue((String) aValue);
				break;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0:
				return ExtraField.ExtrasTypes.class;
			case 1:
			case 2:
				return String.class;
		}
		return String.class;
	}

	public void addRow(ExtraField field) {
		values.add(field);
		fireTableRowsInserted(values.size() - 1, values.size() - 1);
	}

	public void removeRow(int rowIndex) {
		values.remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	public List<ExtraField> getValues() {
		return values;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	public void removeAllRows() {
		int count = values.size();
		values.clear();
		fireTableRowsDeleted(0, count);
	}
}
