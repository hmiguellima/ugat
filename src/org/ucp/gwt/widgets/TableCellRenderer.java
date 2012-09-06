package org.ucp.gwt.widgets;

import com.google.gwt.user.client.ui.*;

public interface TableCellRenderer {
    public Widget getTableCellRendererComponent(MVCTable table, Object value,
					    boolean isSelected, boolean hasFocus, 
					    int row, int column);
	public void setComponentStyle(MVCTable table, Widget component, boolean isSelected, boolean hasFocus, int row, int column);
}
