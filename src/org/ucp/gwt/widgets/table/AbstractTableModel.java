package org.ucp.gwt.widgets.table;

import java.util.*;


public abstract class AbstractTableModel implements TableModel
{
	protected ArrayList listenerList = new ArrayList();

    public String getColumnName(int column) {
		String result = "";
		for (; column >= 0; column = column / 26 - 1) {
		    result = (char)((char)(column%26)+'A') + result;
		}
	        return result;
    }

    public int findColumn(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (columnName.equals(getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	return false;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }
    
    public void appendRow(Object rowObj) {
    }
    
    public void updateRow(int row, Object rowObj) {
    }
    
    public void removeRow(int row) {
    }

    public void addTableModelListener(TableModelListener l) {
    	listenerList.add(l);
    }

    public void removeTableModelListener(TableModelListener l) {
    	listenerList.remove(l);
    }

    public TableModelListener[] getTableModelListeners() {
        return (TableModelListener[])listenerList.toArray();
    }

    public void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this));
    }

    public void fireTableStructureChanged() {
        fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    public void fireTableRowsInserted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
    }

    public void fireTableCellUpdated(int row, int column) {
        fireTableChanged(new TableModelEvent(this, row, row, column));
    }

    public void fireTableChanged(TableModelEvent e) {
		Object[] listeners = listenerList.toArray();
		for (int i=0; i<listeners.length; i++) {
		    if (listeners[i] instanceof TableModelListener) {
		    	((TableModelListener)listeners[i]).tableChanged(e);
		    }
		}
    }

    @Override
	public boolean isAsynchronous() {
		return false;
	}
    
    @Override
    public boolean isColumnVisibleByDefault(int columnIndex) {
    	return true;
    }
} 
