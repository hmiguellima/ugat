package org.ucp.gwt.widgets;

import java.util.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class Calendar extends Composite {
	class Calendar_ChangeListener implements Command {
		int oldDate=-1, oldMonth=-1, oldYear=-1;
		
		public Calendar_ChangeListener(Date date) {
			oldDate=date.getDate();
			oldMonth=date.getMonth()+1;
			oldYear=date.getYear()+1900;
		}
		
		public void execute() {
			String[] dateParts=dateBox.getText().split("-");
			int newDate, newMonth, newYear;
			int eventMask=0;
			
			newYear=Integer.parseInt(dateParts[0]);
			newMonth=Integer.parseInt(dateParts[1]);
			newDate=Integer.parseInt(dateParts[2]);
			
			if (newDate!=oldDate) {
				eventMask=eventMask | DATE_CHANGE;
				oldDate=newDate;
				
			}
			if (newMonth!=oldMonth) {
				eventMask=eventMask | MONTH_CHANGE;
				oldMonth=newMonth;
			}
			if (newYear!=oldYear) {
				eventMask=eventMask | YEAR_CHANGE;
				oldYear=newYear;
			}
			
			fireChangeEvent(getDate(), eventMask);
		}
	}
	
	public final static int DATE_CHANGE=1;
	public final static int MONTH_CHANGE=2;
	public final static int YEAR_CHANGE=4;
	public final static int MONTH_YEAR_CHANGE=6;
	
	private ArrayList calendarChangeListeners=new ArrayList();
	private TextBox dateBox=new TextBox();
	private JavaScriptObject dtObj=null;
	private FlowPanel panel=new FlowPanel();
	private Calendar_ChangeListener changeListener;
	
	public Calendar(Date date) {
		initWidget(panel);
		setStyleName("ucpgwt-Calendar");
		dtObj=initCalendar(panel.getElement());
		setDate(date);
	}
	
	public Calendar(Date date, boolean hideMonthBar) {
		this(date);
		if (hideMonthBar)
			hideMonthBar(dtObj);
	}

	public void addChangeListener(CalendarChangeListener listener) {
		calendarChangeListeners.add(listener);
	}
	
	private void fireChangeEvent(Date date, int eventMask) {
		for (int index=0;index<calendarChangeListeners.size();index++)
			((CalendarChangeListener)(calendarChangeListeners.get(index))).onChange(date, eventMask);
	}
	
	
	private native Element getCalendarElement(JavaScriptObject obj) /*-{
		return obj.getElement();
	}-*/;

	public Date getDate() {
		String[] dateParts=dateBox.getText().split("-");
		
		return new Date(Integer.parseInt(dateParts[0])-1900, Integer.parseInt(dateParts[1])-1, Integer.parseInt(dateParts[2]));
	}
	
	public void hideMonthBar() {
		hideMonthBar(dtObj);
	}
	
	private native void hideMonthBar(JavaScriptObject obj) /*-{
		obj.hideMonthBar();
	}-*/;
	
	protected void onAttach() {
		super.onAttach();
		int width, height;
		width=DOM.getIntAttribute(getCalendarElement(dtObj), "offsetWidth");
		height=DOM.getIntAttribute(getCalendarElement(dtObj), "offsetHeight");
		setWidth(width+"px");
		setHeight(height+"px");
	}
	
	public void removeChangeListener(CalendarChangeListener listener) {
		calendarChangeListeners.remove(listener);
	}

	private native void selectDays(JavaScriptObject obj, String days) /*-{
		obj.selectDays(days);
	}-*/;
	
	public void selectDays(String days) {
		selectDays(dtObj, days);
	}
	
	public void setDate(Date date) {
		changeListener=new Calendar_ChangeListener(date);
		dateBox.setText(String.valueOf(date.getYear()+1900)+"-"+String.valueOf(date.getMonth()+1)+"-"+String.valueOf(date.getDate()));
		showCalendar(dtObj, date.getYear()+1900, date.getMonth()+1, date.getDate(), dateBox.getElement(), (Command)changeListener);
	}
	

	private native JavaScriptObject initCalendar(Element parent) /*-{
		var dt=new $wnd.DatePicker(parent);
		return dt;
	}-*/;
	
	private native void showCalendar(JavaScriptObject obj, int year, int month, int day, Element inputBox, Command onDateChoiceCallback) /*-{
		function callback(dt) {
			inputBox.value=dt.getFullYear()+'-'+(dt.getMonth()+1)+'-'+dt.getDate();
			onDateChoiceCallback.@com.google.gwt.user.client.Command::execute()();
		}
		obj.show(new Date(year, month-1, day), callback, null, callback);
	}-*/;

	public void showMonthBar() {
		showMonthBar(dtObj);
	}
	
	private native void showMonthBar(JavaScriptObject obj) /*-{
		obj.showMonthBar();
	}-*/;
}
