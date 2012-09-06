package org.ucp.gwt.widgets;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class DefaultTableCellEditor implements TableCellEditor {
	class DefaultFocusListener extends DefaultGenericListener implements FocusListener {
		public DefaultFocusListener(MVCTable table, int row, int column, ValueAdapter value) {
			super(table, row, column, value);
		}
		
		public void onFocus(Widget sender) {
		}

		public void onLostFocus(Widget sender) {
			if (table.isEditing()) {
				table.cancelEditMode(false);
				table.getModel().setValueAt(value.getValue(), row, column);
			}
		}
	}
	
	abstract class DefaultGenericListener {
		protected int column;
		protected int row;
		protected MVCTable table;
		protected ValueAdapter value;
		
		public DefaultGenericListener(MVCTable table, int row, int column, ValueAdapter value) {
			this.row=row;
			this.column=column;
			this.table=table;
			this.value=value;
		}
	}
	
	class DefaultKeyboardListener extends DefaultGenericListener implements KeyboardListener {
		public DefaultKeyboardListener(MVCTable table, int row, int column, ValueAdapter value) {
			super(table, row, column, value);
		}

		public void onKeyDown(Widget sender, char keyCode, int modifiers) {
			switch (keyCode) {
				case KeyboardListener.KEY_ENTER:
					table.getModel().setValueAt(value.getValue(), row, column);
					break;
				case KeyboardListener.KEY_ESCAPE:
					table.cancelEditMode(true);
					break;
				case KeyboardListener.KEY_UP:
					table.getModel().setValueAt(value.getValue(), row, column);
					break;
				case KeyboardListener.KEY_DOWN:
					table.getModel().setValueAt(value.getValue(), row, column);
					break;
			}
		}

		public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		}

		public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		}
	}
	
	interface ValueAdapter {
		public Object getValue();
	}
	
	public Widget getTableCellEditorComponent(final MVCTable table, Boolean value, boolean isSelected, final int row, final int column) {
		Boolean checked=new Boolean(!value.booleanValue());
		table.getModel().setValueAt(checked, row, column);
		return null;
	}

	public Widget getTableCellEditorComponent(final MVCTable table, Integer value, boolean isSelected, final int row, final int column) {
		final NumberBox component=new NumberBox(false, true);
		ValueAdapter valueAdapter;
		
		valueAdapter=new ValueAdapter() {
			public Object getValue() {
				return new Integer(component.getText());
			}
		};
		
		component.setText(String.valueOf(value));
		component.addKeyboardListener(new DefaultKeyboardListener(table, row, column, valueAdapter));
		component.addFocusListener(new DefaultFocusListener(table, row, column, valueAdapter));
		
		DeferredCommand.add(new Command() {
			public void execute() {
				component.setFocus(true);
				component.selectAll();
			}
		});
		return component;
	}
	
	public Widget getTableCellEditorComponent(MVCTable table, Object value, boolean isSelected, int row, int column) {
		SimplePanel panel=new SimplePanel();
		Widget w=null;
		if (value instanceof String)
			w=getTableCellEditorComponent(table, (String)value, isSelected, row, column);
		if (value instanceof Integer)
			w=getTableCellEditorComponent(table, (Integer)value, isSelected, row, column);
		if (value instanceof Boolean)
			w=getTableCellEditorComponent(table, (Boolean)value, isSelected, row, column);
		if (value instanceof String[])
			w=getTableCellEditorComponent(table, (String[])value, isSelected, row, column);
		if (w!=null) {
			w.setWidth("100%");
			panel.setWidget(w);
			return panel;
		} else
			return null;
	}

	public Widget getTableCellEditorComponent(final MVCTable table, String value, boolean isSelected, final int row, final int column) {
		final SafeTextBox component=new SafeTextBox();
		ValueAdapter valueAdapter;

		valueAdapter=new ValueAdapter() {
			public Object getValue() {
				return component.getText();
			}
		};
		
		component.setText(value);
		component.addKeyboardListener(new DefaultKeyboardListener(table, row, column, valueAdapter));
		component.addFocusListener(new DefaultFocusListener(table, row, column, valueAdapter));

		DeferredCommand.add(new Command() {
			public void execute() {
				component.setFocus(true);
				component.selectAll();
			}
		});
		return component;
	}
	
	public Widget getTableCellEditorComponent(final MVCTable table, String[] value, boolean isSelected, final int row, final int column) {
		final EditableCombo component=new EditableCombo();
		ValueAdapter valueAdapter;
		
		valueAdapter=new ValueAdapter() {
			public Object getValue() {
				return component.getText();
			}
		};

		
		if (value.length>1) {
			String[] choices=new String[value.length-1]; 
			for (int conta=1;conta<value.length;conta++)
				choices[conta-1]=value[conta];
			component.setChoices(choices, 10);
		}

		component.setText(value[0]);
		component.addKeyboardListener(new DefaultKeyboardListener(table, row, column, valueAdapter));
		
		component.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				table.getModel().setValueAt(component.getText(), row, column);
			}
		});
		
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				component.setFocus(true);
				component.selectAll();
			}
		});
		return component;
	}

}
