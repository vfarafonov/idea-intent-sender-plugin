package ui;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 * Created by vfarafonov on 31.08.2015.
 */
public class ExtrasDeleteButtonEditor extends DefaultCellEditor {


	private final JButton button_;
	private int currentRow_;
	private RemoveRowListener listener_;

	public ExtrasDeleteButtonEditor(RemoveRowListener listener) {
		super(new JCheckBox());
		listener_ = listener;
		button_ = new JButton("Delete");
		button_.setOpaque(true);
		button_.addActionListener(e -> {
			fireEditingStopped();
			if (listener_ != null) {
				listener_.onRowRemoved(currentRow_);
			}
		});
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		currentRow_ = row;
		if (isSelected) {
			button_.setForeground(table.getSelectionForeground());
			button_.setBackground(table.getSelectionBackground());
		} else {
			button_.setForeground(table.getForeground());
			button_.setBackground(table.getBackground());
		}
		return button_;
	}

	public void setListener(RemoveRowListener listener_) {
		this.listener_ = listener_;
	}

	public interface RemoveRowListener {
		void onRowRemoved(int rowIndex);
	}
}
