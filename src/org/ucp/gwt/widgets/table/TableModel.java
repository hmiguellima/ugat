package org.ucp.gwt.widgets.table;

public interface TableModel
{
    public int getRowCount();
    public int getColumnCount();
    public String getColumnName(int columnIndex);
    public boolean isCellEditable(int rowIndex, int columnIndex);
    public Object getValueAt(int rowIndex, int columnIndex);
    public void setValueAt(Object aValue, int rowIndex, int columnIndex);
    public void addTableModelListener(TableModelListener l);
    public void removeTableModelListener(TableModelListener l);
    public void appendRow(Object rowObj);
    public void updateRow(int row, Object rowObj);
    public void removeRow(int row);
    public boolean isAsynchronous();
    public boolean isColumnVisibleByDefault(int columnIndex);
}
