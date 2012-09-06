package org.ucp.gwt.widgets;

/*
 * DefaultTableHeaderRenderer is part of the MVCTable component set.
 * It implements default rendering behavior of header cells.
 */


import com.google.gwt.user.client.ui.*;

public class DefaultTableHeaderCellRenderer implements TableHeaderCellRenderer {
	public Widget getTableHeaderCellRendererComponent(int column, String columnName) {
		Label w=new Label(columnName);
		w.setStyleName("ucpgwt-DefaultTableHeaderCellRenderer");
		return w;
	}
}
