package org.ucp.gwt.widgets;

import org.ucp.gwt.widgets.table.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

public class PaginationPanel extends Composite {
	private TableModel underlyingModel;
	private PaginationModel paginationModel;
	private int rowsByPage;
	private FlowPanel controllerPanel=new FlowPanel();
	private HorizontalPanel pageBtnPanel=new HorizontalPanel();
	private static final int MAX_VISIBLE_PAGES=5;
	private Image prevBtn=new Image(GWT.getModuleBaseURL()+"img/go-previous.png");
	private Image nextBtn=new Image(GWT.getModuleBaseURL()+"img/go-next.png");
	private FlowPanel separatorPanel=new FlowPanel();
	
	public PaginationPanel(TableModel underlyingModel, int rowsByPage) {
		this.underlyingModel=underlyingModel;
		this.rowsByPage=rowsByPage;
		paginationModel=new PaginationModel();
		paginationModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				switch (e.getType()) {
					case TableModelEvent.DELETE:
					case TableModelEvent.INSERT:
						renderController();
						break;
					case TableModelEvent.UPDATE:
						if ( ((e.getFirstRow()==0) && (e.getLastRow()==Integer.MAX_VALUE)) || (e.getFirstRow()==TableModelEvent.HEADER_ROW) )
							renderController();
						break;
				}
			}
		});
		pageBtnPanel.setStyleName("ucpgwt-PaginationPanel-PageBtnPanel");
		controllerPanel.add(pageBtnPanel);
		separatorPanel.setStyleName("ucpgwt-PaginationPanel-SeparatorPanel");
		controllerPanel.add(separatorPanel);
		
		initWidget(controllerPanel);
		controllerPanel.setStyleName("ucpgwt-PaginationPanel");
		renderController();
		prevBtn.setStyleName("ucpgwt-PaginationPanel-NavBtn");
		prevBtn.addClickListener(new PaginationPanel_prevBtn_ClickAdapter());
		nextBtn.setStyleName("ucpgwt-PaginationPanel-NavBtn");
		nextBtn.addClickListener(new PaginationPanel_nextBtn_ClickAdapter());
	}

	private void renderController() {
		int pCount=getPageCount();
		int start, end;
		boolean showNextBtn, showPrevBtn;
		Label pageBtn;
		
		pageBtnPanel.clear();
		if (pCount>1) {
			start=(getPage()/MAX_VISIBLE_PAGES)*MAX_VISIBLE_PAGES;
			end=Math.min(start+MAX_VISIBLE_PAGES-1, getPageCount()-1);
			showPrevBtn=start>=MAX_VISIBLE_PAGES;
			showNextBtn=end<getPageCount()-1;
			
			if (showPrevBtn)
				pageBtnPanel.add(prevBtn);
			for (int index=start;index<=end;index++) {
				final int page=index;
				pageBtn=new Label(String.valueOf(page+1));
				if (page==getPage())
					pageBtn.setStyleName("ucpgwt-PaginationPanel-PageBtn-Selected");
				else {
					pageBtn.setStyleName("ucpgwt-PaginationPanel-PageBtn");
					pageBtn.addClickListener(new ClickListener() {
						public void onClick(Widget sender) {
							setPage(page);
						}
					});
				}
				pageBtnPanel.add(pageBtn);
			}
			if (showNextBtn)
				pageBtnPanel.add(nextBtn);
		}
	}
	
	public int getPageCount() {
		return paginationModel.getPageCount();
	}
	
	public void setPage(int page) {
		paginationModel.setPage(page);
	}
	
	public int getPage() {
		return paginationModel.getPage();
	}
	
	public AbstractTableModel getModel() {
		return paginationModel;
	}
	
	class PaginationPanel_prevBtn_ClickAdapter implements ClickListener {
		public void onClick(Widget sender) {
			setPage(((getPage()/MAX_VISIBLE_PAGES)*MAX_VISIBLE_PAGES)-1);
		}
	}
	
	class PaginationPanel_nextBtn_ClickAdapter implements ClickListener {
		public void onClick(Widget sender) {
			setPage(((getPage()/MAX_VISIBLE_PAGES)*MAX_VISIBLE_PAGES)+MAX_VISIBLE_PAGES);
		}
	}
	
	public class PaginationModel extends AbstractTableModel {
		private int page=0;
		
		public PaginationModel() {
			underlyingModel.addTableModelListener(new TableModelListener() {
				public void tableChanged(TableModelEvent e) {
					int firstRow;
					int lastRow;

					switch (e.getType()) {
						case TableModelEvent.UPDATE:
							if ((e.getFirstRow()==0) && (e.getLastRow()==Integer.MAX_VALUE)) {
								if (underlyingModel.getRowCount()<=rowsByPage*page)
									setPage(0);
								else
									fireTableChanged(e);
							}
							else {
								if (e.getFirstRow()!=-1) {
									// antes de lançar novamente o evento vamos converter as linhas
									firstRow=convertURow(e.getFirstRow());
									lastRow=convertURow(e.getLastRow());
									
									for (int row=firstRow;row<=lastRow;row++)
										if ( (row>=0) && (row<rowsByPage) )
											fireTableChanged(new TableModelEvent((TableModel)(e.getSource()), row, row, TableModelEvent.ALL_COLUMNS)); 
								} else
									fireTableStructureChanged();
							}
							break;
						case TableModelEvent.INSERT: 
							setPage(getPageCount()-1); 
							break;
						case TableModelEvent.DELETE: 
							if (getRowCount()>0)
								fireTableChanged(new TableModelEvent((TableModel)(e.getSource()), 0, Integer.MAX_VALUE, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
							else
								if (page>0)
									setPage(--page);
								else
									fireTableDataChanged();
							break;
					}
				}
			});
		}
		
		private int convertURow(int row) {
			return row-(page*rowsByPage);
		}
		
		public String getColumnName(int column) {
			return underlyingModel.getColumnName(column);
		}

		public int getColumnCount() {
			return underlyingModel.getColumnCount();
		}

		public int getRowCount() {
			int uRowCount=underlyingModel.getRowCount();
			if ((page+1)*rowsByPage>uRowCount) 
				return uRowCount-(rowsByPage*page);
			else
				return rowsByPage;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			int uTopRow=page*rowsByPage;
			return underlyingModel.getValueAt(uTopRow+rowIndex, columnIndex);
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			int uTopRow=page*rowsByPage;
			return underlyingModel.isCellEditable(uTopRow+rowIndex, columnIndex);
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			int uTopRow=page*rowsByPage;
			underlyingModel.setValueAt(aValue, uTopRow+rowIndex, columnIndex);
		}
	
		public void setPage(int page) {
			this.page=page;
			fireTableDataChanged();
		}
		
		public int getPage() {
			return page;
		}
		
		public int getPageCount() {
			return  new Double(Math.ceil((float)(underlyingModel.getRowCount()) / rowsByPage)).intValue(); 
		}

		public void removeRow(int row) {
			int uTopRow=page*rowsByPage;
			underlyingModel.removeRow(uTopRow+row);
		}

		public void updateRow(int row, Object rowObj) {
			int uTopRow=page*rowsByPage;
			underlyingModel.updateRow(uTopRow+row, rowObj);
		}

		public void appendRow(Object rowObj) {
			underlyingModel.appendRow(rowObj);
		}

		public boolean isAsynchronous() {
			return underlyingModel.isAsynchronous();
		}
		
		public TableModel getUnderlyingModel() {
			return underlyingModel;
		}

		@Override
		public boolean isColumnVisibleByDefault(int columnIndex) {
			return underlyingModel.isColumnVisibleByDefault(columnIndex);
		}
	}
}
