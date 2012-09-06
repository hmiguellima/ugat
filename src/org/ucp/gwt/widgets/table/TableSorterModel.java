package org.ucp.gwt.widgets.table;

import java.util.*;

import org.ucp.gwt.widgets.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

public class TableSorterModel extends AbstractTableModel {
    protected TableModel tableModel;

    public static final int DESCENDING = -1;
    public static final int NOT_SORTED = 0;
    public static final int ASCENDING = 1;

    private static Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);

    public static final Comparator DEFAULT_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
        	if ( (o1 instanceof Comparable) && (o1.getClass()==o2.getClass()) )
        		return ((Comparable)o1).compareTo((Comparable)o2);
            return o1.toString().compareTo(o2.toString());
        }
    };

    private Row[] viewToModel;
    private int[] modelToView;

    private MVCTable table;
    private TableListener mouseListener;
    private TableModelListener tableModelListener;
    private Comparator[] columnComparators;
    private List sortingColumns = new ArrayList();

    public TableSorterModel(TableModel tableModel) {
        this.mouseListener = new MouseHandler();
        this.tableModelListener = new TableModelHandler();
        setTableModel(tableModel);
    }

    private void clearSortingState() {
        viewToModel = null;
        modelToView = null;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(TableModel tableModel) {
        if (this.tableModel != null) {
            this.tableModel.removeTableModelListener(tableModelListener);
        }

        this.tableModel = tableModel;
        if (this.tableModel != null) {
            this.tableModel.addTableModelListener(tableModelListener);
            columnComparators=new Comparator[this.tableModel.getColumnCount()];
        }

        clearSortingState();
    }

    public void changeTableHeader(MVCTable table) {
        if (this.table != null) {
            this.table.getTableHeader().removeTableListener(mouseListener);
        }
        this.table=table;
        if (this.table != null) {
            this.table.getTableHeader().addTableListener(mouseListener);
            this.table.setTableHeaderCellRenderer(new SortedHeaderCellRenderer());
        }
    }

    public boolean isSorting() {
        return sortingColumns.size() != 0;
    }

    private Directive getDirective(int column) {
        for (int i = 0; i < sortingColumns.size(); i++) {
            Directive directive = (Directive)sortingColumns.get(i);
            if (directive.column == column) {
                return directive;
            }
        }
        return EMPTY_DIRECTIVE;
    }

    public int getSortingStatus(int column) {
        return getDirective(column).direction;
    }

    private void sortingStatusChanged(boolean fireEvent) {
        clearSortingState();
        if (fireEvent)
        	fireTableStructureChanged();
    }

    public void setSortingStatus(int column, int status) {
        Directive directive = getDirective(column);
        if (directive != EMPTY_DIRECTIVE) {
            sortingColumns.remove(directive);
        }
        if (status != NOT_SORTED) {
            sortingColumns.add(new Directive(column, status));
        }
        sortingStatusChanged(true);
    }

    protected Image getHeaderRendererIcon(int column, int size) {
        Directive directive = getDirective(column);
        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }
        return new Arrow(directive.direction == DESCENDING, size, sortingColumns.indexOf(directive));
    }

    private void cancelSorting() {
        sortingColumns.clear();
        sortingStatusChanged(false);
    }

    public void setColumnComparator(int column, Comparator comparator) {
    	columnComparators[column]=comparator;
    }

    protected Comparator getComparator(int column) {
        Comparator comparator = (Comparator) columnComparators[column];
        if (comparator != null) {
            return comparator;
        }
        return DEFAULT_COMPARATOR;
    }

    private Row[] getViewToModel() {
        if (viewToModel == null) {
            int tableModelRowCount = tableModel.getRowCount();
            viewToModel = new Row[tableModelRowCount];
            for (int row = 0; row < tableModelRowCount; row++) {
                viewToModel[row] = new Row(row);
            }

            if (isSorting()) {
                Arrays.sort(viewToModel);
            }
        }
        return viewToModel;
    }

    public int modelIndex(int viewIndex) {
        return getViewToModel()[viewIndex].modelIndex;
    }

    private int[] getModelToView() {
        if (modelToView == null) {
            int n = getViewToModel().length;
            modelToView = new int[n];
            for (int i = 0; i < n; i++) {
                modelToView[modelIndex(i)] = i;
            }
        }
        return modelToView;
    }

    // TableModel interface methods

    public int getRowCount() {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    public int getColumnCount() {
        return (tableModel == null) ? 0 : tableModel.getColumnCount();
    }

    public String getColumnName(int column) {
        return tableModel.getColumnName(column);
    }

    @Override
    public boolean isColumnVisibleByDefault(int columnIndex) {
    	return tableModel.isColumnVisibleByDefault(columnIndex);
    }
    
    public boolean isCellEditable(int row, int column) {
        return tableModel.isCellEditable(modelIndex(row), column);
    }

    public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(modelIndex(row), column);
    }

    public void setValueAt(Object aValue, int row, int column) {
        tableModel.setValueAt(aValue, modelIndex(row), column);
    }

    // Helper classes

    private class Row implements Comparable {
        private int modelIndex;

        public Row(int index) {
            this.modelIndex = index;
        }

        public int compareTo(Object o) {
            int row1 = modelIndex;
            int row2 = ((Row) o).modelIndex;

            for (Iterator it = sortingColumns.iterator(); it.hasNext();) {
                Directive directive = (Directive) it.next();
                int column = directive.column;
                Object o1 = tableModel.getValueAt(row1, column);
                Object o2 = tableModel.getValueAt(row2, column);

                int comparison = 0;
                // Define null less than everything, except null.
                if (o1 == null && o2 == null) {
                    comparison = 0;
                } else if (o1 == null) {
                    comparison = -1;
                } else if (o2 == null) {
                    comparison = 1;
                } else {
                    comparison = getComparator(column).compare(o1, o2);
                }
                if (comparison != 0) {
                    return directive.direction == DESCENDING ? -comparison : comparison;
                }
            }
            return 0;
        }
    }

    private class TableModelHandler implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            // If we're not sorting by anything, just pass the event along.
            if (!isSorting()) {
                clearSortingState();
                fireTableChanged(e);
                return;
            }

            // If the table structure has changed, cancel the sorting; the
            // sorting columns may have been either moved or deleted from
            // the model.
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                cancelSorting();
                fireTableChanged(e);
                return;
            }

            // We can map a cell event through to the view without widening
            // when the following conditions apply:
            //
            // a) all the changes are on one row (e.getFirstRow() == e.getLastRow()) and,
            // b) all the changes are in one column (column != TableModelEvent.ALL_COLUMNS) and,
            // c) we are not sorting on that column (getSortingStatus(column) == NOT_SORTED) and,
            // d) a reverse lookup will not trigger a sort (modelToView != null)
            //
            // Note: INSERT and DELETE events fail this test as they have column == ALL_COLUMNS.
            //
            // The last check, for (modelToView != null) is to see if modelToView
            // is already allocated. If we don't do this check; sorting can become
            // a performance bottleneck for applications where cells
            // change rapidly in different parts of the table. If cells
            // change alternately in the sorting column and then outside of
            // it this class can end up re-sorting on alternate cell updates -
            // which can be a performance problem for large tables. The last
            // clause avoids this problem.
            int column = e.getColumn();
            if (e.getFirstRow() == e.getLastRow()
                    && column != TableModelEvent.ALL_COLUMNS
                    && getSortingStatus(column) == NOT_SORTED
                    && modelToView != null) {
                int viewIndex = getModelToView()[e.getFirstRow()];
                fireTableChanged(new TableModelEvent(TableSorterModel.this,
                                                     viewIndex, viewIndex,
                                                     column, e.getType()));
                return;
            }

            // Something has happened to the data that may have invalidated the row order.
            clearSortingState();
            fireTableDataChanged();
            return;
        }
    }

    private class MouseHandler implements TableListener {
        public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
            int column = cell;

            if (column != -1) {
                int status = getSortingStatus(column);
                cancelSorting();
                // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or
                // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed.
                //status = status + (e.isShiftDown() ? -1 : 1);
                status++;
                status = (status + 4) % 3 - 1; // signed mod, returning {-1, 0, 1}
                setSortingStatus(column, status);
            }
		}
    }

    private static class Arrow extends Image {
        private int size;

        public Arrow(boolean descending, int size, int priority) {
            this.size = size;
            if (descending)
            	setUrl(GWT.getModuleBaseURL()+"img/sort_down.gif");
            else
            	setUrl(GWT.getModuleBaseURL()+"img/sort_up.gif");
        }

        public int getIconWidth() {
            return size;
        }

        public int getIconHeight() {
            return size;
        }
    }

    private static class Directive {
        private int column;
        private int direction;

        public Directive(int column, int direction) {
            this.column = column;
            this.direction = direction;
        }
    }

	public void appendRow(Object rowObj) {
		tableModel.appendRow(rowObj);
	}
    
	public void removeRow(int row) {
        tableModel.removeRow(modelIndex(row));
	}

	public void updateRow(int row, Object rowObj) {
        tableModel.updateRow(modelIndex(row), rowObj);
	}
	
	class SortedHeaderCellRenderer implements TableHeaderCellRenderer {
		public Widget getTableHeaderCellRendererComponent(int column, String columnName) {
    		FlowPanel container=new FlowPanel();
    		Label columnLabel=new Label(columnName);
    		columnLabel.setStyleName("ucpgwt-TableSorterModelModel-HeaderCellRenderer-Label");
    		Image icon=getHeaderRendererIcon(column, 5);
    		container.add(columnLabel);
    		if (icon!=null) {
    			icon.setStyleName("ucpgwt-TableSorterModelModel-HeaderCellRenderer-Icon");
    			container.add(icon);
    		}
    		container.setStyleName("ucpgwt-TableSorterModelModel-HeaderCellRenderer");
    		container.setTitle("Clique para ordenar");
    		return container;
		}
	}

	public boolean isAsynchronous() {
		if (tableModel==null)
			return false;
		else
			return tableModel.isAsynchronous();
	}
}
