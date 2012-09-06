package org.ucp.gwt.widgets;

import com.google.gwt.user.client.ui.*;

public interface TableCellEditor {
	public Widget getTableCellEditorComponent(MVCTable table,
            Object value,
            boolean isSelected,
            int row,
            int column);
}
