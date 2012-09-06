package org.ucp.gwt.widgets;

import java.util.*;

import org.ucp.gwt.util.*;
import org.ucp.gwt.widgets.eventscalendar.*;
import org.ucp.gwt.widgets.table.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;

public class EventsCalendarTable extends MVCTable {
	class EventsCalendarTable_ColumnResizeListener implements ColumnResizeListener {
		Timer resizeEventTimer;
		
		public EventsCalendarTable_ColumnResizeListener() {
			resizeEventTimer=new Timer() {
				public void run() {
					getCalendarModel().showMonth();
				}
			};
		}
		
		public void onColumnResize(int column, int width) {
			resizeEventTimer.cancel();
			resizeEventTimer.schedule(2000);
		}
	}
	
	class EventsCalendarTable_EventOptionsListener extends MouseListenerAdapter implements EventListener {
		private AbstractCalendarEvent calEvent;
		
		public EventsCalendarTable_EventOptionsListener(AbstractCalendarEvent calEvent) {
			this.calEvent=calEvent;
		}
		
		public void onBrowserEvent(Event event) {
			if (DOM.eventGetType(event)==Event.ONCLICK) 
				fireEventClickEvent(calEvent, DOM.eventGetClientX(event), DOM.eventGetClientY(event));
		}
		
		public void onMouseUp(Widget sender, int x, int y) {
			fireEventClickEvent(calEvent, sender.getAbsoluteLeft()+x, sender.getAbsoluteTop()+y);
		}
		
	}
	
	class EventsCalendarTable_RendererDynamicPosition implements DynamicPosition {
		private UIObject panel;

		public EventsCalendarTable_RendererDynamicPosition(UIObject panel) {
			this.panel=panel;
		}
		
		public int getLeft() {
			return panel.getAbsoluteLeft();
		}

		public int getTop() {
			int eventPanelTop, tablePanelTop, windowTop, maxTop;
			
			eventPanelTop=panel.getAbsoluteTop();
			tablePanelTop=DOM.getAbsoluteTop(DOM.getParent(getTableDataPanel().getElement()));
			windowTop=WindowUtils.getScrollTop();
			
			maxTop=Math.max(eventPanelTop, tablePanelTop);
			maxTop=Math.max(maxTop, windowTop);

			return maxTop;
		}
	}
	
	class EventsCalendarTable_ControlPanel extends FlowPanel {
		private Image btnNext=new Image(GWT.getModuleBaseURL()+"img/btn_next.gif");
		private Image btnPrev=new Image(GWT.getModuleBaseURL()+"img/btn_prev.gif");
		private HorizontalPanel hPanel=new HorizontalPanel();
		private Label caption=new Label();
		
		public EventsCalendarTable_ControlPanel() {
			setStyleName("ucpgwt-EventsCalendarTable-ControlPanel");
			btnPrev.setStyleName("ucpgwt-EventsCalendarTable-ControlPanel-NavBtn");
			btnPrev.setTitle("Semana anterior");
			hPanel.add(btnPrev);
			btnNext.setStyleName("ucpgwt-EventsCalendarTable-ControlPanel-NavBtn");
			btnNext.setTitle("Semana seguinte");
			hPanel.add(btnNext);
			caption.setStyleName("ucpgwt-EventsCalendarTable-ControlPanel-Caption");
			hPanel.add(caption);
			hPanel.setSpacing(2);
			add(hPanel);
			
			btnPrev.addClickListener(new ClickListener() {
				public void onClick(Widget sender) {
					if (!getCalendarModel().prevWeek())
						firePrevMonthEvent();
				}
			});

			btnNext.addClickListener(new ClickListener() {
				public void onClick(Widget sender) {
					if (!getCalendarModel().nextWeek())
						fireNextMonthEvent();
				}
			});
		}
		
		public void setCaption(String text) {
			caption.setText(text);
		}
	}
	
	class EventsCalendarTable_eventRenderer implements TableCellRenderer {
		public Widget getTableCellRendererComponent(final MVCTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			CalendarEventList eventList=(CalendarEventList)value;
			Label panel=new Label();
			
			if ( (getCalendarModel().getWeekDayDate(column-1).getMonth()!=getCalendarModel().getDate().getMonth()) ||
					(getCalendarModel().getWeekDayDate(column-1).getYear()!=getCalendarModel().getDate().getYear()) ) 
				panel.setStyleName("ucpgwt-EventsCalendarTable-DisabledCellRenderer");
			else
				panel.setStyleName("ucpgwt-DefaultTableCellRenderer");
				
			if (eventList!=null)
				DeferredCommand.add(new EventsCalendarTable_EventRenderer_OnRender(eventList, row, column));
			
			return panel;
		}

		public void setComponentStyle(MVCTable table, Widget component, boolean isSelected, boolean hasFocus, int row, int column) {
		}
	}
	
	class EventsCalendarTable_EventRenderer_OnRender implements Command {
		int column;
		CalendarEventList eventList;
		int row;
		
		public EventsCalendarTable_EventRenderer_OnRender(CalendarEventList eventList, int row, int column) {
			this.eventList=eventList;
			this.row=row;
			this.column=column;
		}
		
		public void execute() {
			Element rowEl, cellEl;
			int rowHeight, left, top, width, height, horizontalOffset;
			int topTSOffset, bottomTSOffset, tsStart, tsEnd, tsHeight;
			int topEventOffset, bottomEventOffset;
			int eventSlotSpan;
			float topRatio, bottomRatio;
			FocusPanel eventPanel;
			FlowPanel eventContainerPanel;
			Label eventWhatLabel;
			Element eventHeaderLabel;
			AbstractCalendarEvent event;
			Image opLink;

			try {
				cellEl=getTableData().getCellFormatter().getElement(row, column);
				rowEl=getTableData().getRowFormatter().getElement(row);
				rowHeight=DOMUtils.getOffsetHeight(rowEl);
				tsStart=getCalendarModel().getTimeSlotStart(row);
				tsHeight=getCalendarModel().calcTimeSlotMinutes();

				for (int index=0;index<eventList.size();index++) {
					event=eventList.getEvent(index);
					eventContainerPanel=new FlowPanel();
					eventPanel=new FocusPanel();
					eventPanel.setStyleName("ucpgwt-EventsCalendarTable-EventRenderer");
					if (event.getBackColorOverride()!=null)
						DOM.setStyleAttribute(eventPanel.getElement(), "backgroundColor", event.getBackColorOverride());
					eventHeaderLabel=DOM.createDiv();
					DOM.setInnerText(eventHeaderLabel, event.getStartShortTime());
					DOM.setAttribute(eventHeaderLabel, "className", "ucpgwt-EventsCalendarTable-EventRenderer-HeaderLabel");
					if (event.getHeaderBackColorOverride()!=null)
						DOM.setStyleAttribute(eventHeaderLabel, "backgroundColor", event.getHeaderBackColorOverride());
					eventWhatLabel=new Label(event.getWhat());
					eventWhatLabel.setStyleName("ucpgwt-EventsCalendarTable-EventRenderer-WhatLabel");
					opLink=new Image(EVENT_OPTIONS_IMG_URL);
					opLink.setStyleName("ucpgwt-EventsCalendarTable-EventRenderer-OpcoesLabel");
					
					tsEnd=getCalendarModel().getTimeSlotEnd(getCalendarModel().calcEndTimeSlotIndex(event));
					horizontalOffset=event.getOffset()*HORIZONTALOFFSET_MULTIPLIER;

					// Calculate topEventOffset
					topTSOffset=(event.getStart().getHour()*60+event.getStart().getMinute())-tsStart;
					if (topTSOffset==0)
						topEventOffset=0;
					else {
						topRatio=(float)topTSOffset/tsHeight;
						topEventOffset=(int)(rowHeight*topRatio);
					}
					// Calculate bottomEventOffset
					bottomTSOffset=tsEnd-(event.getEnd().getHour()*60+event.getEnd().getMinute());
					if (bottomTSOffset==0)
						bottomEventOffset=0;
					else {
						bottomRatio=(float)bottomTSOffset/tsHeight;
						bottomEventOffset=(int)(rowHeight*bottomRatio);
					}
					eventSlotSpan=getCalendarModel().calcEventTimeSlotSpan(event);
					
					left=DOM.getIntAttribute(cellEl, "offsetLeft")+2;
					top=DOM.getIntAttribute(cellEl, "offsetTop")+1+topEventOffset;
					width=DOMUtils.getOffsetWidth(cellEl)-5-(horizontalOffset);
					// Protect the event from disapearing
					if (width<5)
						width=5;
					height=(rowHeight*eventSlotSpan)-4-topEventOffset-bottomEventOffset;
					DOM.setStyleAttribute(eventPanel.getElement(), "top", top+"px");
					DOM.setStyleAttribute(eventPanel.getElement(), "left", left+"px");
					DOM.setStyleAttribute(eventPanel.getElement(), "width", width+"px");
					DOM.setStyleAttribute(eventPanel.getElement(), "height", height+"px");
					
					getTableDataPanel().add(eventPanel);

					if (height>rowHeight)
						DOM.appendChild(eventContainerPanel.getElement(), eventHeaderLabel);
					eventPanel.setWidget(eventContainerPanel);
					eventContainerPanel.add(eventWhatLabel);
					eventContainerPanel.add(opLink);
					eventWhatLabel.addMouseListener(new TooltipMouseListener(new EventsCalendarTable_RendererDynamicPosition(eventPanel), event.getTitle(), event.getTip()));
					opLink.addMouseListener(new EventsCalendarTable_EventOptionsListener(event));

					eventRendererList.add(eventPanel);
				}
			} catch (Exception ex) {
				Window.alert("EventsCalendarTable_EventRenderer_OnRender::execute:"+ex.getMessage());
			}
		}
	}
	
	class EventsCalendarTable_TimeSlotHeaderRenderer implements TableCellRenderer {
		public Widget getTableCellRendererComponent(MVCTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Widget widget=new Label((String)value);
			widget.setStyleName("ucpgwt-EventsCalendarTable-TimeSlotHeaderRenderer");
			return widget;
		}

		public void setComponentStyle(MVCTable table, Widget component, boolean isSelected, boolean hasFocus, int row, int column) {
		}
	}
	
	private static final int HORIZONTALOFFSET_MULTIPLIER=15;
	private static final String EVENT_OPTIONS_IMG_URL=GWT.getModuleBaseURL()+"img/options.png";
	private EventsCalendarTable_ControlPanel controlPanel=new EventsCalendarTable_ControlPanel();
	private EventsCalendarTable_eventRenderer eventRenderer;
	private EventsCalendarTable_TimeSlotHeaderRenderer timeSlotHeaderRenderer;
	private ArrayList eventRendererList=new ArrayList();
	private ArrayList changeEventListeners=new ArrayList();
	private EventsCalendarTable_ColumnResizeListener columnResizeListener=new EventsCalendarTable_ColumnResizeListener();
	
	public EventsCalendarTable() {
		super(false, true);

		Element parentEl;
		
		setIncrementalRendering(false);
		setStyleName("ucpgwt-EventsCalendarTable");
		getTableData().setCellSpacing(0);
		getTableData().setStyleName("ucpgwt-EventsCalendarTable-TableData");
		getTableHeader().setCellSpacing(0);
		getTableHeader().addStyleName("ucpgwt-EventsCalendarTable-TableHeader");
		((ResizableTable)getTableHeader()).addColumnResizeListener(columnResizeListener);
		DOM.setStyleAttribute(getTableDataPanel().getElement(), "overflow", "auto");
		DOM.setStyleAttribute(getTableDataPanel().getElement(), "position", "relative");
		
		timeSlotHeaderRenderer=new EventsCalendarTable_TimeSlotHeaderRenderer();
		eventRenderer=new EventsCalendarTable_eventRenderer();
		dockPanel.add(controlPanel, DockPanel.NORTH);
		parentEl=DOM.getParent(getTableHeader().getElement());
		DOM.insertChild(parentEl, controlPanel.getElement(), 0);
	}
	
	public EventsCalendarModel getCalendarModel() {
		return (EventsCalendarModel)getModel();
	}

	private void initTable() {
		if (getCalendarModel().getCalendarMode()==EventsCalendarModel.CALENDARMODE_WEEK) {
			setColumnAlignment(0, MVCTable.ALIGN_CENTER);
			setColumnWidth(0, 40);
			setRowHeight(40);
			setColumnCellRenderer(0, timeSlotHeaderRenderer);
			for (int column=1;column<getModel().getColumnCount();column++)
				setColumnCellRenderer(column, eventRenderer);
		} else {
			
		}
	}

	public void setModel(TableModel dataModel) throws Exception {
		if (!(dataModel instanceof EventsCalendarModel))
			throw new Exception("EventsCalendarTabel::setModel:dataModel must be a EventsCalendarModel");
		super.setModel(dataModel);
		controlPanel.setCaption(((EventsCalendarModel)dataModel).getWeekCaption());
		initTable();
		cleanUpRenderers();
	}
	
	public void setModel(TableModel dataModel, boolean isSorted) throws Exception {
		throw new Exception("EventsCalendarTable::Method not suported");
	}

	public void setModel(TableModel dataModel, boolean isSorted, int rowsByPage) throws Exception {
		throw new Exception("EventsCalendarTable::Method not suported");
	}
	
	public void setTableDataHeight(int height) {
		DOM.setStyleAttribute(getTableDataPanel().getElement(), "height", height+"px");
	}

	//Clean-up renderers
	private void cleanUpRenderers() {
		for (int index=0;index<eventRendererList.size();index++) 
			getTableDataPanel().remove((Widget)eventRendererList.get(index));
		eventRendererList.clear();
	}
	
	public void tableChanged(TableModelEvent e) {
		cleanUpRenderers();
		controlPanel.setCaption(getCalendarModel().getWeekCaption());
		renderAll();
	}
	
	public void addChangeEventListener(CalendarEventChangeListener listener) {
		changeEventListeners.add(listener);
	}
	
	public void removeChangeEventListener(CalendarEventChangeListener listener) {
		changeEventListeners.remove(listener);
	}
	
	protected void firePrevMonthEvent() {
		for (int index=0;index<changeEventListeners.size();index++)
			((CalendarEventChangeListener)(changeEventListeners.get(index))).onPrevMonth();
	}
	
	protected void fireNextMonthEvent() {
		for (int index=0;index<changeEventListeners.size();index++)
			((CalendarEventChangeListener)(changeEventListeners.get(index))).onNextMonth();
	}
	
	protected void fireEventClickEvent(AbstractCalendarEvent event, int x, int y) {
		for (int index=0;index<changeEventListeners.size();index++)
			((CalendarEventChangeListener)(changeEventListeners.get(index))).onEventClick(event, x, y);
	}
}
