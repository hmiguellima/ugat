package org.ucp.gwt.widgets;

/*
 * DefaultTableCellRenderer is part of the MVCTable component set.
 * It implements default rendering behavior of data cells.
 */

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class DefaultTableCellRenderer implements TableCellRenderer {
	public Widget getTableCellRendererComponent(MVCTable table, Boolean value, boolean isSelected, boolean hasFocus, int row, int column) {
		Widget component=new HTML((value.booleanValue()==true) ? "<center><img src='"+GWT.getModuleBaseURL()+"img/checked.gif'></img></center>" : "<center><img src='"+GWT.getModuleBaseURL()+"img/unchecked.gif'></img></center>");
		setComponentStyle(table, component, isSelected, hasFocus, row, column);
		return component;
	}

	public Widget getTableCellRendererComponent(MVCTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Boolean)
			return getTableCellRendererComponent(table, (Boolean)value, isSelected, hasFocus, row, column);
		if (value instanceof String[])
			return getTableCellRendererComponent(table, (String[])value, isSelected, hasFocus, row, column);
		Widget component;
		if ( (value!=null) && (value.toString().trim().length()>0) ) {
			component=new Label(value.toString());
		} else {
			component=new HTML("&nbsp;");
		}
		setComponentStyle(table, component, isSelected, hasFocus, row, column);
		return component;
	}

	public Widget getTableCellRendererComponent(MVCTable table, String[] value, boolean isSelected, boolean hasFocus, int row, int column) {
		Widget component;
		if ( (value[0]!=null) && (value[0].toString().trim().length()>0) ) {
			component=new Label(value[0].toString());
		} else {
			component=new HTML("&nbsp;");
		}
		setComponentStyle(table, component, isSelected, hasFocus, row, column);
		return component;
	}
	
	public void setComponentStyle(MVCTable table, Widget component, boolean isSelected, boolean hasFocus, int row, int column) {
		if (!isSelected) {
			component.setTitle("Clique para seleccionar");
			DOM.setAttribute(component.getElement(), "className", "ucpgwt-DefaultTableCellRenderer");
		} else {
			if (table.getModel().isCellEditable(row, column))
				component.setTitle("Clique para editar");
			if (hasFocus)
				DOM.setAttribute(component.getElement(), "className", "ucpgwt-DefaultTableCellRenderer-HasFocus");
			else
				DOM.setAttribute(component.getElement(), "className", "ucpgwt-DefaultTableCellRenderer");
		}
	}
}
