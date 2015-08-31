package ui;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Created by vfarafonov on 31.08.2015.
 */
public class ExtrasDeleteButtonRenderer extends JButton implements TableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setText("Delete");
		return this;
	}
}
