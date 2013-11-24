package org.ucp.gwt.widgets;

/*
 * MVCTable is the main class of a set of components designed for
 * building Model View Controller type tabular data widgets based on 
 * the concept of Swing's JTable.
 * 
 * It's main features are:
 * - MVC design based on JTable's.
 * - Sortable columns.
 * - Client side pagination support.
 * - In-place editing with keyboard and mouse.
 * - Separation of rendering and editing views/behaviours.
 * - Extendable cell renderer/editor architecture that provides customablity
 *   of individual cells appearance/editing based on data type.
 * - Default support for the following data types:
 *     String: Basic one line text editing.
 *     String[]: Editable combo editing.
 *     Integer: Number editing.
 *     Boolean: Check box editing/rendering.
 * - Header and Data event listeners.
 * - Fully customizable look.    
 * - Focusable and keyboard navigatable (using arrow keys).
 *     
 */

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ucp.gwt.util.*;
import org.ucp.gwt.widgets.table.*;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.*;

@SuppressWarnings("deprecation")
public class MVCTable extends Composite implements TableModelListener, ColumnResizeListener, KeyDownHandler, KeyPressHandler, KeyUpHandler {
	private class ExportBtnClickHandler implements ClickListener {
		public void onClick(Widget sender) {
			exportToExcel();
		}
	}
	
	private class ColumnsSelectionMenu extends MenuBar {
		private Widget parentMenu;
		
		@Override
		protected void onLoad() {
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					PopupPanel popup;
					
					popup=(PopupPanel)(ColumnsSelectionMenu.this.getParent().getParent());
					DOM.setStyleAttribute(popup.getElement(), "left", String.valueOf(popup.getAbsoluteLeft()-popup.getOffsetWidth()+parentMenu.getOffsetWidth())+"px");
				}
			});
		}

		public ColumnsSelectionMenu(Widget parentMenu) {
			super(true);
			this.parentMenu=parentMenu;
		}
		
	}
	
	private class LoaderPanel extends FlowPanel {
		private Label caption=new Label();
		
		public LoaderPanel() {
			setStyleName("ucpgwt-MVCTable-LoaderPanel");
			caption.setText("A processar...");
			caption.setStyleName("ucpgwt-MVCTable-LoaderPanel-Caption");
			add(caption);
		}
	}
	
	private class RowCountBox extends HorizontalPanel {
		private Label label=new Label("Resultados por página: ");
		private ListBox listBox=new ListBox();
		
		public RowCountBox(int[] rowsArray) {
			for (int index=0;index<rowsArray.length;index++)
				listBox.addItem(String.valueOf(rowsArray[index]));
			listBox.setVisibleItemCount(1);
			listBox.setSelectedIndex(0);
			setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			setSpacing(2);
			add(label);
			add(listBox);
			setStyleName("ucpgwt-MVCTable-RowCountBox");
		}
		
		public void addChangeListener(ChangeListener listener) {
			listBox.addChangeListener(listener);
		}
		
		public int getSelectedValue() {
			int value=0;
			
			try {
				value=Integer.parseInt(listBox.getItemText(listBox.getSelectedIndex()));
			} catch (Exception ex) {
			}
			return value;
		}
		
		public void removeChangeListener(ChangeListener listener) {
			listBox.removeChangeListener(listener);
		}
	}
	
	private class TableDataListener implements TableListener {
		public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
			handleCellClicked(sender, row, cell);
		}
	}
	
	private class TableFocusListener implements FocusListener {
		public void onFocus(Widget sender) {
			//System.out.println("onFocus|"+selectedRow+"|"+selectedColumn);
			tableDataHasFocus=true;
			if ( (dataModel!=null) && (!editMode) )
				if (dataModel.getRowCount()>0) {
					if (selectedRow==-1) 
						selectedRow=0;
					if (selectedColumn==-1)
						selectedColumn=0;
					updateCellStyle(selectedRow, selectedColumn, selectedRow, selectedColumn);
				}
		}

		public void onLostFocus(Widget sender) {
			//System.out.println("onLostFocus|"+selectedRow+"|"+selectedColumn);
			tableDataHasFocus=false;
			if (!editMode) 
				updateCellStyle(selectedRow, selectedColumn, selectedRow, -1);
		} 
	}
	
	public class TableFocusPanel extends Composite {
		private FlowPanel flowPanel;
		private FocusPanel focusPanel;
		
		public TableFocusPanel() {
			focusPanel=new FocusPanel();
			flowPanel=new FlowPanel();
			focusPanel.setWidget(flowPanel);
			initWidget(focusPanel);
			setStyleName("ucpgwt-MVCTable-TableFocusPanel");
		}

		public void add(Widget widget) {
			flowPanel.add(widget);
		}
		
		public void addFocusListener(FocusListener listener) {
			focusPanel.addFocusListener(listener);
		}

		public void remove(Widget widget) {
			flowPanel.remove(widget);
		}
		
		public void setFocus(boolean focused) {
			focusPanel.setFocus(focused);
		}
		
		public void setWidget(Widget widget) {
			flowPanel.clear();
			flowPanel.add(widget);
		}
	}
	public static final HasHorizontalAlignment.HorizontalAlignmentConstant ALIGN_CENTER=HasHorizontalAlignment.ALIGN_CENTER;
	public static final HasHorizontalAlignment.HorizontalAlignmentConstant ALIGN_LEFT=HasHorizontalAlignment.ALIGN_LEFT;
	public static final HasHorizontalAlignment.HorizontalAlignmentConstant ALIGN_RIGHT=HasHorizontalAlignment.ALIGN_RIGHT;
	public static final HasHorizontalAlignment.HorizontalAlignmentConstant DEFAULT_COLUMN_ALIGNMENT=HasHorizontalAlignment.ALIGN_LEFT;
	public static final int DEFAULT_COLUMN_WIDTH=100;
	public static final int NAVIGATE_DOWN=3;
	public static final int NAVIGATE_LEFT=0;
	public static final int NAVIGATE_RIGHT=1;
	public static final int NAVIGATE_UP=2;
	private static final String HIDDEN_COLUMN_STYLE="ucpgwt-MVCTable-HiddenColumn";
	private static final String COLUMN_SELECTBOX_CHECKED="<img src='img/checked.gif'/>";
	private static final String COLUMN_SELECTBOX_UNCHECKED="<img src='img/unchecked.gif'/>";
	private HasHorizontalAlignment.HorizontalAlignmentConstant[] columnAlignmentArray;
	private TableCellEditor[] columnEditorArray;
	private TableCellRenderer[] columnRendererArray;
	private int[] columnWidthArray;
	private FlowPanel container;
	private CellFormatter dataCellFormatter;
	private TableModel dataModel=null;
	private TableHeaderCellRenderer defaultHeaderRenderer; 
	private DefaultTableCellEditor defaultTableCellEditor; 
	private DefaultTableCellRenderer defaultTableCellRenderer; 
	protected DockPanel dockPanel=new DockPanel(); 
	private boolean editEnabled=true;
	private boolean editMode=false;
	private Widget editorWidget=null;
	private LoaderPanel loaderPanel=new LoaderPanel();
	private HorizontalPanel optionsPanel=new HorizontalPanel();
	private PaginationPanel paginationPanel=null;
	private int rowHeight=22;
	private int selectedColumn=-1;
	private int selectedRow=-1;
	private FlexTable tableData=new FlexTable();
	private boolean tableDataHasFocus=false;
	private ArrayList<TableListener> tableDataListenerList=new ArrayList<TableListener>();
	private TableFocusPanel tableDataPanel=new TableFocusPanel();
	private FlexTable tableHeader;
	private String title=null;
	private FlowPanel titlePanel=null;
	private Label titleLabel=null;
	private FlowPanel topPanel;
	private HashMap<Integer, MenuItem> columnsMenuItemMap=new HashMap<Integer, MenuItem>();
	private boolean incrementalRendering=true;
	private TableSorterModel sortedModel=null;
	private ImageButton exportBtn=new ImageButton("img/excel-btn.gif");
	private FlowPanel menuPanel=new FlowPanel();

	
	public MVCTable() {
		this(true);
	}

	public MVCTable(boolean isEditEnabled) {
		this(isEditEnabled, true, null);
	}
	
	public MVCTable(boolean isEditEnabled, boolean isResizable) {
		this(isEditEnabled, isResizable, null);
	}
	
	public MVCTable(boolean isEditEnabled, boolean isResizable, String title) {
		this.editEnabled=isEditEnabled;
		this.title=title;

		exportBtn.setTitle("Exportar para Excel");
		
		container=new FlowPanel();
		container.setStyleName("ucpgwt-MVCTable-Container");

		if (title!=null) {
			topPanel=new FlowPanel();
			topPanel.setStyleName("ucpgwt-MVCTable-TopPanel");
			titlePanel=new FlowPanel();
			titlePanel.setStyleName("ucpgwt-MVCTable-TitlePanel");
			titleLabel=new Label(title);
			titlePanel.add(titleLabel);
			optionsPanel=new HorizontalPanel();
			optionsPanel.setStyleName("ucpgwt-MVCTable-OptionsPanel");
			topPanel.add(titlePanel);
			topPanel.add(optionsPanel);
			container.add(topPanel);
		}

		container.add(dockPanel);
		initWidget(container);

		if (isResizable) {
			tableHeader=new ResizableTable();
			((ResizableTable)tableHeader).addColumnResizeListener(this);
		} else
			tableHeader=new FlexTable();
		DOM.setElementAttribute(tableHeader.getElement(), "cellspacing", "0");
		addStyleName("ucpgwt-MVCTable");
		dockPanel.addStyleName("ucpgwt-MVCTable-DockPanel");
		tableData.setStyleName("ucpgwt-MVCTable-DataTable");
		DOM.setElementAttribute(tableData.getElement(), "cellspacing", "0");
		tableHeader.addStyleName("ucpgwt-MVCTable-HeaderTable");
		tableDataPanel.setWidget(tableData);
		dockPanel.add(tableHeader, DockPanel.NORTH);
		dockPanel.add(tableDataPanel, DockPanel.CENTER);
		defaultHeaderRenderer=new DefaultTableHeaderCellRenderer();
		defaultTableCellRenderer=new DefaultTableCellRenderer();
		defaultTableCellEditor=new DefaultTableCellEditor();
		if (editEnabled) {
			tableData.addTableListener(new TableDataListener());
			tableDataPanel.addFocusListener(new TableFocusListener());
		}
		dataCellFormatter=tableData.getCellFormatter();
		
		// Handlers
		addDomHandler(this, KeyDownEvent.getType());
		addDomHandler(this, KeyPressEvent.getType());
		addDomHandler(this, KeyUpEvent.getType());
		exportBtn.addClickListener(new ExportBtnClickHandler());
	}
	
	public void addTableListener(TableListener listener) {
		tableDataListenerList.add(listener);
	}

	public void handleCellClicked(SourcesTableEvents sender, int row, int cell) {
		this.handleCellClicked(sender, row, cell, false);
	}
	
	public void handleCellClicked(SourcesTableEvents sender, int row, int cell, boolean forceRedraw) {
		if ( (dataModel==null) || (dataModel.getRowCount()==0))
			return;
		if ( (row!=selectedRow) || (cell!=selectedColumn) || forceRedraw ) {
			if ( (row!=selectedRow) && (selectedRow!=-1) )
				renderRow(selectedRow, false);
			editMode=false;
			selectedRow=row;
			selectedColumn=cell;
			
			renderRow(selectedRow, true);

			if (!tableDataHasFocus)
				tableDataPanel.setFocus(true);
		} else {
			if (!editMode) {
				updateCellStyle(selectedRow, selectedColumn, selectedRow, -1);
				selectedColumn=cell;
				if (!tableDataHasFocus)
					tableDataPanel.setFocus(true);
				else
					updateCellStyle(selectedRow, selectedColumn, selectedRow, selectedColumn);
				enterEditMode();
			} 
		}
		
		// lets call the registered listeners
		fireCellClicked(sender, row, cell);
	}
	
	public void fireCellClicked(SourcesTableEvents sender, int row, int cell) {
		for (TableListener listener:tableDataListenerList)
			listener.onCellClicked(sender, row, cell);
	}
	
	public void cancelEditMode(boolean gainFocus) {
		editMode=false;
		editorWidget=null;
		renderRow(selectedRow, true);
		if (gainFocus) {
			DeferredCommand.addCommand(new Command(){
				public void execute() {
					tableDataPanel.setFocus(true);
				}
			});
		} else
			selectedColumn=-1;
	}
	
	private void enterEditMode() {
		if ( (selectedRow==-1) || (selectedColumn==-1) ) return;
		if (columnWidthArray[selectedColumn]==0) return; 
		if (!editMode && dataModel.isCellEditable(selectedRow, selectedColumn)) {
			editorWidget=columnEditorArray[selectedColumn].getTableCellEditorComponent(MVCTable.this, dataModel.getValueAt(selectedRow, selectedColumn), false, selectedRow, selectedColumn);
			if (editorWidget!=null) {
				editMode=true;
				setDataCellWidget(selectedRow, selectedColumn, editorWidget);
				editorWidget.setPixelSize(columnWidthArray[selectedColumn], rowHeight);
			}
		}
	}
	
	public int getColumnWidth(int column) {
		return columnWidthArray[column];
	}
	
	public TableModel getModel() {
		return dataModel;
	}
	
	public int getRowHeight() {
		return rowHeight;
	}
	
	public int getSelectedRow() {
		return selectedRow;
	}
	
	public FlexTable getTableData() {
		return tableData;
	}

	protected TableFocusPanel getTableDataPanel() {
		return tableDataPanel;
	}
	
	public FlexTable getTableHeader() {
		return tableHeader;
	}
	
	public Widget getWidgetAt(int x, int y) {
		int totalWidth=0;
		boolean outOfBounds=true;
		int realTop;
		int scrollTop;
		
		scrollTop=DOM.getElementPropertyInt(tableDataPanel.getElement(), "scrollTop");
		realTop=tableData.getAbsoluteTop()+scrollTop;
		x=x-tableData.getAbsoluteLeft();
		y=(y-realTop)+scrollTop;
		y=(int)(Math.round(Math.floor((new Float(y)).floatValue()/(rowHeight+6))));
		if ( (dataModel==null) || (x<0) || (y<0) ) return null;
		for (int index=0;index<dataModel.getColumnCount();index++) {
			totalWidth+=columnWidthArray[index]+6;
			if (x<totalWidth) {
				x=index;
				outOfBounds=false;
				break;
			}
		}
		if (outOfBounds) return null;
		return tableData.getWidget(y, x);
	}
	
	public boolean isEditing() {
		return editMode;
	}

	public void navigate(int direction) {
		switch (direction) {
			case NAVIGATE_LEFT:
				if (selectedColumn>0)
					selectedColumn--;
				break;
			case NAVIGATE_RIGHT:
				if (selectedColumn<dataModel.getColumnCount()-1)
					selectedColumn++;
				break;
			case NAVIGATE_UP:
				if (selectedRow>0)
					selectedRow--;
				break;
			case NAVIGATE_DOWN:
				if (selectedRow<dataModel.getRowCount()-1)
					selectedRow++;
				break;
		}
	}
	
	public void onColumnResize(int column, int width) {
		setColumnWidth(column, width);
		if (tableData.getRowCount()>0) 
			renderRow(0, getSelectedRow()==0);
	}
	
	public void removeTableListener(TableListener listener) {
		tableDataListenerList.remove(listener);
	}
	
	protected void renderAll() {
		renderHeader();
		renderData();
	}

	protected void renderData() {
		final int modelRowCount;
		int tableRowCount;
		
		tableDataPanel.setWidget(loaderPanel);
		tableRowCount=tableData.getRowCount();
		modelRowCount=dataModel.getRowCount();
		while (tableRowCount-->modelRowCount)
			tableData.removeRow(0);
		
		if (incrementalRendering)
			DeferredCommand.addCommand(new IncrementalCommand() {
				int row=0;
	
				public boolean execute() {
					if (modelRowCount==0)
						renderRow(0, false);
					else
						if (row==selectedRow)
							renderRow(row, true);
						else
							renderRow(row, false);
					if (++row<modelRowCount)
						return true;
					else {
						tableDataPanel.setWidget(tableData);
						return false;
					}
				}
			});
		else
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					if (modelRowCount==0)
						renderRow(0, false);
					else
						for (int row=0;row<modelRowCount;row++) {
							if (row==selectedRow)
								renderRow(row, true);
							else
								renderRow(row, false);
						}
					tableDataPanel.setWidget(tableData);
				}
			});
	}
	
	private void renderHeader() {
		for (int column=0;column<dataModel.getColumnCount();column++) {
			Widget w=defaultHeaderRenderer.getTableHeaderCellRendererComponent(column, dataModel.getColumnName(column));
			tableHeader.setWidget(0, column, w);
			DOM.setElementProperty(tableHeader.getCellFormatter().getElement(0, column), "className", "ucpgwt-MVCTable-HeaderCell");
			if (columnWidthArray[column]!=0)
				tableHeader.getCellFormatter().setWidth(0, column, columnWidthArray[column]+"px");
		}
	}
	
	public void renderRow(int row, boolean isSelected) {
		Widget w;
		boolean hasRows;
		Element rowElement;
		int colCount;
		
		hasRows=(dataModel.getRowCount()>0);
		colCount=dataModel.getColumnCount();
		
		for (int column=0;column<colCount;column++) {
			if (hasRows)
				w=columnRendererArray[column].getTableCellRendererComponent(this, dataModel.getValueAt(row, column), isSelected, (isSelected && (selectedColumn==column) ), row, column);
			else 
				w=new Label("");
			setDataCellWidget(row, column, w);
			if (row==0)	
				if (columnWidthArray[column]!=0)
					dataCellFormatter.setWidth(row, column, String.valueOf(columnWidthArray[column])+"px");
			if (hasRows && dataModel.isCellEditable(row, column)) 
				DOM.setElementProperty(dataCellFormatter.getElement(row, column), "className", "ucpgwt-MVCTable-DataCellEditable");
			else
				DOM.setElementProperty(dataCellFormatter.getElement(row, column), "className", "ucpgwt-MVCTable-DataCell");
			dataCellFormatter.setHorizontalAlignment(row, column, columnAlignmentArray[column]);
		}
		
		rowElement=tableData.getRowFormatter().getElement(row);
		DOM.setStyleAttribute(rowElement, "height", String.valueOf(rowHeight)+"px");
		if (isSelected)
			DOM.setElementProperty(rowElement, "className", "ucpgwt-MVCTable-SelectedRow");
		else
			if (row % 2==0)
				DOM.setElementProperty(rowElement, "className", "ucpgwt-MVCTable-EvenRow");
			else
				DOM.setElementProperty(rowElement, "className", "ucpgwt-MVCTable-OddRow");
	}
	
	public void setColumnAlignment(int column, HasHorizontalAlignment.HorizontalAlignmentConstant alignment) {
		columnAlignmentArray[column]=alignment;
	}
	
	public void setColumnCellEditor(int column, TableCellEditor editor) {
		columnEditorArray[column]=editor;
	}
	
	public void setColumnCellRenderer(int column, TableCellRenderer renderer) {
		columnRendererArray[column]=renderer;
	}
	
	public void setColumnWidth(int column, int width) {
		String columnWidth;
		
		if (width<=0)
			width=1;
		//if ( (columnWidthArray[column]==0) && (width>0) ) 
		//	showColumn(column);
		columnWidthArray[column]=width;
		//if (width==0) 
		//	hideColumn(column);
		columnWidth=String.valueOf(columnWidthArray[column])+"px";
		tableHeader.getCellFormatter().setWidth(0, column, columnWidth);
	}
	
	public void showColumn(int column) {
		tableData.getColumnFormatter().removeStyleName(column, HIDDEN_COLUMN_STYLE);
		tableData.insertRow(0);
		tableData.removeRow(0);
		tableHeader.getColumnFormatter().removeStyleName(column, HIDDEN_COLUMN_STYLE);
		tableHeader.insertRow(0);
		tableHeader.removeRow(0);
		if (columnsMenuItemMap.containsKey(column))
			columnsMenuItemMap.get(column).setHTML(COLUMN_SELECTBOX_CHECKED+getModel().getColumnName(column));
	}
	
	public void hideColumn(int column) {
		tableData.getColumnFormatter().addStyleName(column, HIDDEN_COLUMN_STYLE);
		tableData.insertRow(0);
		tableData.removeRow(0);
		tableHeader.getColumnFormatter().addStyleName(column, HIDDEN_COLUMN_STYLE);
		tableHeader.insertRow(0);
		tableHeader.removeRow(0);
		if (columnsMenuItemMap.containsKey(column))
			columnsMenuItemMap.get(column).setHTML(COLUMN_SELECTBOX_UNCHECKED+getModel().getColumnName(column));
	}
	
	public boolean isColumnVisible(int column) {
		return (tableHeader.getColumnFormatter().getStyleName(column).indexOf(HIDDEN_COLUMN_STYLE)<0);
	}
	
	private void setDataCellWidget(int row, int column, Widget widget) {
		try {
			tableData.setWidget(row, column, widget);
		} catch (Exception e) {
			Window.alert("MVCTable::setDataCellWidget:"+e.getMessage());
		}
	}
	
	private void setModelAndLayout(final TableModel dataModel, boolean resetLayout) throws Exception {
		if (dataModel==null)
			throw new Exception("MVCTable::setModel:model não pode ser null");

        if (this.dataModel != dataModel) {
    	    TableModel old = this.dataModel;

    	    if (old != null) 
                old.removeTableModelListener(this);
            this.dataModel = dataModel;
            dataModel.addTableModelListener(this);
        }

        if (resetLayout) {
    		int columnCount=dataModel.getColumnCount();

    		columnRendererArray=new TableCellRenderer[columnCount];
    		columnEditorArray=new TableCellEditor[columnCount];

    		columnWidthArray=new int[columnCount];
    		columnAlignmentArray=new HasHorizontalAlignment.HorizontalAlignmentConstant[columnCount];
    		
    		for (int column=0;column<dataModel.getColumnCount();column++) { 
    			columnWidthArray[column]=DEFAULT_COLUMN_WIDTH;
    			columnRendererArray[column]=defaultTableCellRenderer;
    			columnEditorArray[column]=defaultTableCellEditor;
    			columnAlignmentArray[column]=DEFAULT_COLUMN_ALIGNMENT;
    		}
        }
		
		DeferredCommand.add(new Command() {
			public void execute() {
				renderHeader();
				renderRow(0, false);
	    		for (int column=0;column<dataModel.getColumnCount();column++) 
	    			if (!dataModel.isColumnVisibleByDefault(column))
	    				hideColumn(column);
			}
		});
		// If the table's model isn't async then it's because it's data is ready,
		// then let's show it
		if (!dataModel.isAsynchronous()) {
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					renderData();
				}
			});
		} 
	}

	public void setModel(TableModel dataModel) throws Exception {
		setModel(dataModel, false, null);
	}

	public void setModel(TableModel dataModel, boolean isSorted) throws Exception {
		setModel(dataModel, isSorted, null);
	}

	
	public void setModel(TableModel dataModel, boolean isSorted, int rowsByPage) throws Exception {
		setModel(dataModel, isSorted, new int[]{rowsByPage});
	}
	
	public void setModel(TableModel dataModel, final boolean isSorted, int[] rowsByPage) throws Exception {
		TableModel auxModel;
		
		if (paginationPanel!=null) 
			container.remove(paginationPanel);

		if (title!=null) {
			optionsPanel.clear();
			addExportButton();
		}
		
		editMode=false;
		sortedModel=null;
		selectedRow=-1;

		if (isSorted) {
			sortedModel=new TableSorterModel(dataModel);
			auxModel=sortedModel;
		} else 
			auxModel=dataModel;
		
		if ( (rowsByPage!=null) && (rowsByPage.length>0) ) {
			paginationPanel=new PaginationPanel(auxModel, rowsByPage[0]);
			auxModel=paginationPanel.getModel();

			setModelAndLayout(auxModel, true);
			
			container.add(paginationPanel);
			columnsMenuItemMap.clear();			
			if (title!=null) {
				if (rowsByPage.length>1)
					addRowCountBox(isSorted, rowsByPage, ((PaginationPanel.PaginationModel)auxModel).getUnderlyingModel());
				addColumnSelectionBox();
			}
		} else
			setModelAndLayout(auxModel, true);
		
		if (isSorted)
			sortedModel.changeTableHeader(this);
	}

	private void addExportButton() {
		optionsPanel.add(exportBtn);
	}
	
	private void addRowCountBox(final boolean isSorted, int[] rowsByPage, final TableModel underlyingModel) {
		final RowCountBox rowCountBox;

		rowCountBox=new RowCountBox(rowsByPage);
		rowCountBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				try {
					container.remove(paginationPanel);
					paginationPanel=new PaginationPanel(underlyingModel, rowCountBox.getSelectedValue());
					setModelAndLayout(paginationPanel.getModel(), false);
					
					if (isSorted)
						((TableSorterModel)underlyingModel).changeTableHeader(MVCTable.this);
					
					container.add(paginationPanel);
				} catch (Exception ex) {
					Window.alert("MVCTable::setModel:"+ex.getMessage());
				}
			}
		});
		optionsPanel.add(rowCountBox);
	}
	
	private void addColumnSelectionBox() {
		MenuBar menuBar=new MenuBar();
		MenuBar columnsSelectionMenu;
		MenuItem colsMenuItem;
		
		optionsPanel.remove(menuPanel);
		menuPanel=new FlowPanel();
		
		columnsSelectionMenu=new ColumnsSelectionMenu(menuBar);
		colsMenuItem=new MenuItem("...", columnsSelectionMenu);
		menuBar.addItem(colsMenuItem);
		colsMenuItem.setTitle("Colunas");
		for (int index=0;index<getModel().getColumnCount();index++) {
			final int colInt=index;
			final String columnName;
			final MenuItem colItem;
			String html;
			
			columnName=getModel().getColumnName(index);
			
			if (columnName.length() > 0) {
				html=COLUMN_SELECTBOX_CHECKED+columnName;
				colItem=new MenuItem(html, true, new Command() {
					public void execute() {
					}
				});
				colItem.setCommand(new Command() {
					public void execute() {
						String html;

						if (isColumnVisible(colInt)) {
							hideColumn(colInt);
							html=COLUMN_SELECTBOX_UNCHECKED+columnName;
						} else {
							showColumn(colInt);
							html=COLUMN_SELECTBOX_CHECKED+columnName;
						}
						colItem.setHTML(html);
					}
				});
				columnsMenuItemMap.put(colInt, colItem);
				columnsSelectionMenu.addItem(colItem);
			}
		}
		
		menuPanel.add(menuBar);
		menuPanel.addStyleName("ucpgwt-MVCTable-ColumnsSelectionBox");
		columnsSelectionMenu.addStyleName("ucpgwt-MVCTable-ColumnsSelectionBox-Menu");
		optionsPanel.add(menuPanel);
	}
	
	public void setRowHeight(int rowHeight) {
		this.rowHeight=rowHeight;
	}

	public void setSelectedRow(int row) {
		if (row!=selectedRow) {
			if ( (selectedRow!=-1) && (selectedRow<tableData.getRowCount()) )
				renderRow(selectedRow, false);
			if (row!=-1)
				renderRow(row, true);
			selectedRow=row;
			editMode=false;
		}
	}
	
	public void setTableHeaderCellRenderer(TableHeaderCellRenderer renderer) {
		defaultHeaderRenderer=renderer;
	}

	private void updateStructure() {
		int columnCount=dataModel.getColumnCount();
		TableCellRenderer[] columnRenderers;
		TableCellEditor[] columnEditors;
		int[] columnWidths;
		HasHorizontalAlignment.HorizontalAlignmentConstant[] columnAlignments;

		columnRenderers=new TableCellRenderer[columnCount];
		columnEditors=new TableCellEditor[columnCount];

		columnWidths=new int[columnCount];
		columnAlignments=new HasHorizontalAlignment.HorizontalAlignmentConstant[columnCount];
		
		for (int column=0;column<dataModel.getColumnCount();column++) {
			if (column<columnWidthArray.length) {
				columnWidths[column]=columnWidthArray[column];
				columnRenderers[column]=columnRendererArray[column];
				columnEditors[column]=columnEditorArray[column];
				columnAlignments[column]=columnAlignmentArray[column];
			} else {
				columnWidths[column]=DEFAULT_COLUMN_WIDTH;
				columnRenderers[column]=defaultTableCellRenderer;
				columnEditors[column]=defaultTableCellEditor;
				columnAlignments[column]=DEFAULT_COLUMN_ALIGNMENT;
			}
		}
		
		columnWidthArray=columnWidths;
		columnRendererArray=columnRenderers;
		columnEditorArray=columnEditors;
		columnAlignmentArray=columnAlignments;

		renderHeader();
		renderRow(0, false);
		for (int column=0;column<dataModel.getColumnCount();column++) 
			if (!dataModel.isColumnVisibleByDefault(column))
				hideColumn(column);
		renderData();
	}
	
	public void tableChanged(TableModelEvent e) {
		try {
			switch (e.getType()) {
				case TableModelEvent.UPDATE:
					if ((e.getFirstRow()==0) && (e.getLastRow()==Integer.MAX_VALUE)) {
						selectedRow=-1;
						renderData();
					} else {
						if (e.getFirstRow()==TableModelEvent.HEADER_ROW) {
							if (title!=null)
								addColumnSelectionBox();
							updateStructure();
						} else
							renderRow(e.getFirstRow(), e.getFirstRow()==selectedRow);
					}
					break;
				case TableModelEvent.INSERT:
					renderData();
					break;
				case TableModelEvent.DELETE:
					tableData.removeRow(e.getFirstRow());
					selectedRow=-1;
					if (e.getFirstRow()==0)
						renderRow(0, false);
					
					break;
			}
			if (editMode) cancelEditMode(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger logger = Logger.getLogger("ugat");
			logger.log(Level.SEVERE, "MVCTable::tableChanged"+ex.getMessage());
		}
	}
	
	protected void updateCellStyle(int row, int column, int selectedRow, int selectedColumn) {
		boolean isSelected;

		if ( (row!=-1) && (column!=-1) ) {
			isSelected=(row==selectedRow);
			columnRendererArray[column].setComponentStyle(this, tableData.getWidget(row, column), isSelected, (isSelected && (selectedColumn==column) ), row, column); 		
		}
	}

	public Label getTitleLabel() {
		return titleLabel;
	}

	public void setIncrementalRendering(boolean incrementalRendering) {
		this.incrementalRendering = incrementalRendering;
	}
	
	public void setColumnComparator(int column, Comparator comparator) {
		if (sortedModel!=null)
			sortedModel.setColumnComparator(column, comparator);
	}
	
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		this.title=title;
		if (titleLabel!=null)
			titleLabel.setText(title);
	}
	
	public void exportToExcel() {
		StringBuilder dataBuilder=new StringBuilder();
		TableModel model;
		Object value;
		String valueStr;
		
		if (getModel() instanceof PaginationPanel.PaginationModel)
			model=((PaginationPanel.PaginationModel)getModel()).getUnderlyingModel();
		else
			model=getModel();
		dataBuilder.append("<table>");
		dataBuilder.append("<caption>"+title+"</caption>");
		dataBuilder.append("<tr>");
		for (int column=0;column<model.getColumnCount();column++) {
			dataBuilder.append("<th>");
			dataBuilder.append(model.getColumnName(column));
			dataBuilder.append("</th>");
		}
		dataBuilder.append("</tr>");
			
		for (int row=0;row<model.getRowCount();row++) {
			dataBuilder.append("<tr>");
			for (int column=0;column<model.getColumnCount();column++) {
				value=model.getValueAt(row, column);
				if (value instanceof String[])
					valueStr=((String[])value)[0];
				else
					valueStr=(value!=null?value.toString():"");
				dataBuilder.append("<td>");
				dataBuilder.append(valueStr);
				dataBuilder.append("</td>");
			}
			dataBuilder.append("</tr>");
		}
		dataBuilder.append("</table>");
		
		WindowUtils.openDataURI("application/vnd.ms-excel", dataBuilder.toString());
	}

	public void onKeyDown(KeyDownEvent event) {
	    char key='\0';

		if (!editEnabled) return;
		
    	key=(char)event.getNativeKeyCode();
    	switch (key) {
			case KeyCodes.KEY_RIGHT:
				if (!editMode) {
					navigate(NAVIGATE_RIGHT);
					renderRow(selectedRow, true);
					event.preventDefault();
				}
    	  		return;
    	  	case KeyCodes.KEY_LEFT:
    	  		if (!editMode) {
					navigate(NAVIGATE_LEFT);
					renderRow(selectedRow, true);
					event.preventDefault();
    	  		}
    	  		return;
    	  	case KeyCodes.KEY_UP:
				renderRow(selectedRow, false);
				navigate(NAVIGATE_UP);
				renderRow(selectedRow, true);
				event.preventDefault();
    	  		return;
    	  	case KeyCodes.KEY_DOWN:
				renderRow(selectedRow, false);
				navigate(NAVIGATE_DOWN);
				renderRow(selectedRow, true);
				event.preventDefault();
    	  		return;
    	  	case KeyCodes.KEY_ENTER:
				event.preventDefault();
				event.stopPropagation();
    	  		return;
		}
	}

	public void onKeyPress(KeyPressEvent event) {
	    char key='\0';
	    char keyCode=(char)event.getNativeEvent().getKeyCode();
	    
	    key=Character.toUpperCase(event.getCharCode());
    	if ( ((key>'A') && (key<'Z')) ||
    		 ((key>'0') && (key<'9')) ||
    		 (key==' ') || (keyCode==KeyCodes.KEY_ENTER) ) {
    		enterEditMode();
    		if (keyCode==KeyCodes.KEY_ENTER) { 
    			event.preventDefault();
				event.stopPropagation();
    		}
    	}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode()==KeyCodes.KEY_ENTER)  {
			event.preventDefault();
			event.stopPropagation();
		}
	}
}