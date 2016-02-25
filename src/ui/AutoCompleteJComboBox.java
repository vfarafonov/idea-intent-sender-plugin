package ui;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import diff.CaseInsensitiveHashSet;
import diff.Searchable;

/**
 * Created by vfarafonov on 01.01.2016.
 */
public class AutoCompleteJComboBox extends JComboBox {
	private static final int POSITION_NOT_DEFINED = -1;

	private Searchable searchable_;
	private String currentText_;
	private int lastCaretPosition_ = POSITION_NOT_DEFINED;

	public AutoCompleteJComboBox(Searchable searchable) {
		super();
		this.searchable_ = searchable;
		setEditable(true);
		Component component = getEditor().getEditorComponent();
		if (component instanceof JTextComponent) {
			final JTextComponent textComponent = (JTextComponent) component;
			textComponent.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					currentText_ = textComponent.getText();
					update();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					currentText_ = textComponent.getText();
					update();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					currentText_ = textComponent.getText();
				}

				public void update() {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							lastCaretPosition_ = textComponent.getCaretPosition();
							String query = textComponent.getText();
							Set<String> founds = new CaseInsensitiveHashSet(searchable_.search(query));
							setEditable(false);
							removeAllItems();
							addItem(query);
							if (founds.contains(query)) {
								// Already added it
								founds.remove(query);
							}
							for (String found : founds) {
								addItem(found);
							}
							setEditable(true);
							textComponent.requestFocus();
						}
					});
				}
			});

			// Do not hide popup
			textComponent.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					if (textComponent.getText().length() > 0 && getItemCount() > 1) {
						setPopupVisible(true);
					}
					if (lastCaretPosition_ != POSITION_NOT_DEFINED) {
						textComponent.setCaretPosition(lastCaretPosition_);
						lastCaretPosition_ = POSITION_NOT_DEFINED;
					}
				}

				@Override
				public void focusLost(FocusEvent e) {
				}
			});
		} else {
			throw new IllegalStateException("Editing component is not a JTextComponent!");
		}
		putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
		setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	}

	public String getText() {
		return currentText_;
	}

	public void setText(String text) {
		getEditor().setItem(text);
	}
}
