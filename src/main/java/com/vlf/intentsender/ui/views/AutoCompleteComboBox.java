package com.vlf.intentsender.ui.views;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import com.vlf.intentsender.utils.IntentActionsHelper;

/**
 * Created by vfarafonov on 09.03.2016.
 */
public class AutoCompleteComboBox extends JComboBox {

	private Model model_ = new Model();
	private String previousPattern_;
	private boolean modelFilling_;

	public AutoCompleteComboBox() {
		setEditable(true);
		setPattern(null);

		JTextComponent textComponent_ = (JTextComponent) getEditor().getEditorComponent();
		textComponent_.setDocument(new AutoCompleteDocument());
		setModel(model_);
		setSelectedItem(null);

		putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
		setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	}

	public String getText() {
		return getEditor().getItem().toString();
	}

	public void setText(String text) {
		getEditor().setItem(text);
	}

	private void setPattern(String pattern) {
		if (pattern != null && pattern.trim().isEmpty()) {
			pattern = null;
		}
		if (previousPattern_ == null && pattern == null ||
				pattern != null && pattern.equals(previousPattern_)) {
			return;
		}
		previousPattern_ = pattern;
		modelFilling_ = true;
		model_.setPattern(pattern);
		modelFilling_ = false;
		setPopupVisible(model_.getSize() > 0);
	}

	public synchronized void addToTop(String text) {
		model_.addToTop(text);
	}

	private class AutoCompleteDocument extends PlainDocument {

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			if (modelFilling_) {
				return;
			}
			super.remove(offs, len);
			setPopupVisible(false);
			updateModel();
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (modelFilling_) {
				return;
			}
			super.insertString(offs, str, a);
			String text = getText(0, getLength());
			if (!text.equals(getSelectedItem())) {
				updateModel();
			}
		}

		private void updateModel() throws BadLocationException {
			String textToMatch = getText(0, getLength());
			setPattern(textToMatch);
		}
	}

	private class Model extends AbstractListModel implements ComboBoxModel {
		Data data_ = new Data();
		private String selected_;

		public Model() {
			for (String action : IntentActionsHelper.getActionsList()) {
				data_.add(action);
			}
		}

		@Override
		public Object getSelectedItem() {
			return selected_;
		}

		@Override
		public void setSelectedItem(Object anItem) {
			if ((selected_ != null && !selected_.equals(anItem)) || selected_ == null && anItem != null) {
				selected_ = (String) anItem;
				fireContentsChanged(this, -1, -1);
			}
		}

		@Override
		public int getSize() {
			return data_.getFilteredList_().size();
		}

		@Override
		public Object getElementAt(int index) {
			return (index >= 0 && index < getSize()) ? data_.getFilteredList_().get(index) : "";
		}

		public void addToTop(String text) {
			if (text == null || text.isEmpty()) {
				return;
			}
			if (data_.size() == 0) {
				data_.add(text);
			} else {
				data_.addToTop(text);
			}

			setPattern(null);
			model_.setSelectedItem(text);
		}

		public void setPattern(String pattern) {
			int sizeBefore = getSize();
			data_.setPattern(pattern);
			int sizeAfter = getSize();
			if (sizeBefore < sizeAfter) {
				fireIntervalAdded(this, sizeBefore, sizeAfter - 1);
				fireContentsChanged(this, 0, sizeBefore - 1);
			} else if (sizeAfter < sizeBefore) {
				fireIntervalRemoved(this, sizeAfter, sizeBefore - 1);
				fireContentsChanged(this, 0, sizeAfter - 1);
			}
		}

		class Data {
			private final List<String> list_ = new ArrayList<String>();
			private List<String> filteredList_ = new ArrayList<String>();

			void add(String s) {
				list_.add(s);
			}

			void addToTop(String s) {
				list_.add(0, s);
			}

			void remove(int index) {
				list_.remove(index);
			}

			List<String> getList() {
				return list_;
			}

			public List<String> getFilteredList_() {
				return filteredList_;
			}

			int size() {
				return list_.size();
			}

			void setPattern(String pattern) {
				if (pattern == null || pattern.isEmpty()) {
					filteredList_.clear();
					filteredList_.addAll(list_);
					setSelectedItem(model_.getElementAt(0));
				} else {
					filteredList_.clear();
					for (String item : list_) {
						if (item.toLowerCase().contains(pattern.toLowerCase()) && !item.toLowerCase().equalsIgnoreCase(pattern)) {
							filteredList_.add(item);
						}
					}
					setSelectedItem(pattern);
				}
			}

			boolean contains(String s) {
				if (s == null || s.isEmpty()) {
					return true;
				}
				for (String item : list_) {
					if (item.equalsIgnoreCase(s)) {
						return true;
					}
				}
				return false;
			}
		}
	}
}
