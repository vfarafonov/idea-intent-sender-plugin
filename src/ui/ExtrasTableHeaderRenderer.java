package ui;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Created by vfarafonov on 02.09.2015.
 */
public class ExtrasTableHeaderRenderer extends DefaultTableCellRenderer {
	public ExtrasTableHeaderRenderer() {
		setHorizontalAlignment(CENTER);
		setHorizontalTextPosition(LEFT);
		setVerticalAlignment(BOTTOM);
		setOpaque(false);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		JTableHeader tableHeader = table.getTableHeader();
		if (tableHeader != null){
			setForeground(tableHeader.getForeground());
		}
		setText("asdasd");
		return this;
	}
}
