package org.ucp.gwt.widgets.eventscalendar;

import java.util.*;
import org.ucp.gwt.widgets.table.*;


public class EventsCalendarModel extends AbstractTableModel  {
	public static final int CALENDARMODE_MONTH=1;
	public static final int CALENDARMODE_WEEK=0;
	public static final int FRIDAY=4;
	public static final int MONDAY=0;
	public static final int SATURDAY=5;
	public static final int SUNDAY=6;
	public static final int THURSDAY=3;
	protected static final int TIMESLOTS=24;
	public static final int TUESDAY=1;
	public static final int WEDNESDAY=2;
	public static final String[] WEEKDAYS={"Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"};
	public static final String[] MONTHNAMES={"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
	private int calendarMode;
	private Date date;
	private int daysInMonth;
	private CalendarEventList[][] monthArray;
	private int year, month;
	
	public EventsCalendarModel(final AbstractCalendarEvent[] eventData, Date date, int calendarMode) {
		initModel(eventData, date, calendarMode);
	}

	private void initModel(final AbstractCalendarEvent[] eventData, Date date, int calendarMode) {
		this.calendarMode=calendarMode;
		this.date=date;
		year=date.getYear();
		month=date.getMonth();
		daysInMonth=getDaysInMonth(date.getYear(), date.getMonth());
		
		monthArray=new CalendarEventList[daysInMonth][TIMESLOTS];
		for (int dayIndex=0;dayIndex<daysInMonth;dayIndex++) 
			for (int timeSlotIndex=0;timeSlotIndex<TIMESLOTS;timeSlotIndex++)
				monthArray[dayIndex][timeSlotIndex]=null;
		processCalendarData(eventData);
	}

	public void updateModel(final AbstractCalendarEvent[] eventData, Date date, int calendarMode) {
		initModel(eventData, date, calendarMode);
		fireTableStructureChanged();
	}
	
	public int calcEndTimeSlotIndex(AbstractCalendarEvent event) {
		int timeSlotIndex;
		
		timeSlotIndex=event.getEnd().getHour()*60+event.getEnd().getMinute();
		if ((timeSlotIndex*TIMESLOTS)%(24*60)==0)
			return (timeSlotIndex*TIMESLOTS)/(24*60)-1;
		else
			return (timeSlotIndex*TIMESLOTS)/(24*60);
	}
	
	public int calcTimeSlotMinutes() {
		return (24*60)/TIMESLOTS;
	}
	
	public int calcEventTimeSlotSpan(AbstractCalendarEvent event) {
		return calcEndTimeSlotIndex(event)-calcStartTimeSlotIndex(event)+1;
	}

	public int calcStartTimeSlotIndex(AbstractCalendarEvent event) {
		int timeSlotIndex;
		
		timeSlotIndex=event.getStart().getHour()*60+event.getStart().getMinute();
		timeSlotIndex=(timeSlotIndex*TIMESLOTS)/(24*60);
		return timeSlotIndex;
	}
	
	protected void calcOrder() {
		CalendarEventList eventList;
		AbstractCalendarEvent event;
		int offset;
		
		for (int dayIndex=0;dayIndex<daysInMonth;dayIndex++) {
			for (int timeSlotIndex=0;timeSlotIndex<TIMESLOTS;timeSlotIndex++) {
				eventList=monthArray[dayIndex][timeSlotIndex];
				if (eventList!=null) {
					Collections.sort(eventList);
					for (int index=0;index<eventList.size();index++) {
						event=eventList.getEvent(index);
						offset=getEventOverlapMaxOffset(event, dayIndex, timeSlotIndex)+1;
						event.setOffset(offset);
					}
				}
			}
		}
	}

	public int getCalendarMode() {
		return calendarMode;
	}
	
	public int getColumnCount() {
		if (calendarMode==CALENDARMODE_WEEK)
			return 8;
		else
			return 0;
	}
	
	public String getColumnName(int column) {
		Date date;
		String shortDate;
		if (calendarMode==CALENDARMODE_WEEK) {
			if (column==0)
				return "";
			else {
				date=getWeekDayDate(column-1);
				shortDate=date.getDate()+"/"+String.valueOf(date.getMonth()+1);
				return WEEKDAYS[column-1]+" "+shortDate;
			}
		} else
			return "";
	}

	public Date getDate() {
		return date;
	}
	
	public int getDaysInMonth() {
		return daysInMonth;
	}

	private int getDaysInMonth(int year, int month) {
		Date dd = new Date(year, month+1, 0);
		return dd.getDate();
	}

	protected int getEventOverlapMaxOffset(AbstractCalendarEvent event, int dayIndex, int maxTimeSlot) {
		CalendarEventList eventList;
		AbstractCalendarEvent testEvent;
		Date startEventTestDate, endEventTestDate;
		int eventYear, eventMonth, eventDate;
		int maxOffset=-1;
		
		eventYear=event.getStart().getDate().getYear();
		eventMonth=event.getStart().getDate().getMonth();
		eventDate=event.getStart().getDate().getDate();
		for (int timeIndex=0;timeIndex<=maxTimeSlot;timeIndex++) {
			eventList=monthArray[dayIndex][timeIndex];
			if (eventList!=null) {
				for (int index=0;index<eventList.size();index++) {
					testEvent=eventList.getEvent(index);
					// Assuming events are ordered by time, let's optimize here
					if (testEvent==event)
						break;
					startEventTestDate=new Date(eventYear, eventMonth, eventDate, testEvent.getStart().getHour(), testEvent.getStart().getMinute());
					endEventTestDate=new Date(eventYear, eventMonth, eventDate, testEvent.getEnd().getHour(), testEvent.getEnd().getMinute());
					if  ( (startEventTestDate.getTime()<=event.getStart().getDate().getTime()) &&
						  (endEventTestDate.getTime()>event.getStart().getDate().getTime()) ) {
						if (maxOffset<testEvent.getOffset())
							maxOffset=testEvent.getOffset();
					}
				}
			}
		}
		return maxOffset;
	}
	
	public CalendarEventList[][] getMonthArray() {
		return monthArray;
	}

	public int getRowCount() {
		if (calendarMode==CALENDARMODE_WEEK)
			return TIMESLOTS;
		else
			return 0;
	}

	public int getTimeSlotEnd(int timeSlot) {
		return ((timeSlot+1)*(24*60))/TIMESLOTS;
	}

	public int getTimeSlotStart(int timeSlot) {
		if (timeSlot==0)
			return 0;
		else
			return (timeSlot*(24*60))/TIMESLOTS;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Date weekDayDate;
		
		if (calendarMode==CALENDARMODE_WEEK) {
			if (columnIndex==0) {
				if (rowIndex<10)
					return "0"+String.valueOf(rowIndex)+":00";
				else
					return String.valueOf(rowIndex)+":00";
			} else {
				weekDayDate=getWeekDayDate(columnIndex-1);
				if ( (weekDayDate.getMonth()==date.getMonth()) &&
					 (weekDayDate.getYear()==date.getYear()) )
					return monthArray[weekDayDate.getDate()-1][rowIndex];
			}
		}
		return null;
	}
	
	public Date getWeekDayDate(int weekDayIndex) {
		int weekDay;
		Date weekDayDate;

		weekDay=date.getDay()-1;
		if (weekDay<0)
			weekDay=6;
		weekDayDate=new Date(date.getTime());
		weekDayDate.setDate(weekDayDate.getDate()-weekDay+weekDayIndex);
		return weekDayDate;
	}
	
	private void processCalendarData(AbstractCalendarEvent[] eventData) {
		AbstractCalendarEvent event;
		int dayIndex;
		int timeSlotIndex;
		long monthStart, monthEnd;
		Date eventStartDate, eventEndDate;
		
		monthStart=(new Date(date.getYear(), date.getMonth(), 1).getTime());
		monthEnd=(new Date(date.getYear(), date.getMonth()+1, 0, 23, 59).getTime());
		for (int conta=0;conta<eventData.length;conta++) {
			event=eventData[conta];
			eventStartDate=event.getStart().getDate();
			eventEndDate=event.getEnd().getDate();
			// Test if event intercepts month
			if ( (eventStartDate.getTime()<=monthEnd) && (eventEndDate.getTime()>=monthStart) ) {
				// Clip event if out of month bounds
				if (eventStartDate.getTime()<monthStart)
					eventStartDate=new Date(monthStart);
				if (eventEndDate.getTime()>monthEnd)
					eventEndDate=new Date(monthEnd);

				for (dayIndex=eventStartDate.getDate()-1;dayIndex<eventEndDate.getDate();dayIndex++) {
					timeSlotIndex=calcStartTimeSlotIndex(event);
					if (monthArray[dayIndex][timeSlotIndex]==null) {
						monthArray[dayIndex][timeSlotIndex]=new CalendarEventList();
					}
					monthArray[dayIndex][timeSlotIndex].add(event.clone());
				}
			}
		}
		calcOrder();
	}
	
	public void showMonth() {
		fireTableStructureChanged();
	}

	public void showWeek(int day) {
		date.setDate(day);
		fireTableStructureChanged();
	}
	
	public boolean prevWeek() {
		if ( (getWeekDayDate(0).getMonth()!=month) || (date.getDate()==1) ) {
			return false;
		} else {
			date.setDate(date.getDate()-7);
			if (date.getMonth()!=month) {
				date.setDate(1);
				date.setMonth(month);
				date.setYear(year);
			} 
			fireTableStructureChanged();
			return true;
		}
	}
	
	public boolean nextWeek() {
		if ( (getWeekDayDate(6).getMonth()!=month) || (date.getDate()==daysInMonth) ) {
			return false;
		} else {
			date.setDate(date.getDate()+7);
			if (date.getMonth()!=month) {
				date.setDate(daysInMonth);
				date.setMonth(month);
				date.setYear(year);
			}
			fireTableStructureChanged();
			return true;
		}
	}

	public boolean isAsynchronous() {
		return false;
	}
	
	public String getWeekCaption() {
		String startDateStr, endDateStr;
		Date startDate, endDate;
		
		startDate=getWeekDayDate(0);
		endDate=getWeekDayDate(6);
		startDateStr=startDate.getDate()+"/"+MONTHNAMES[startDate.getMonth()]+" de "+String.valueOf(startDate.getYear()+1900);
		endDateStr=endDate.getDate()+"/"+MONTHNAMES[endDate.getMonth()]+" de "+String.valueOf(endDate.getYear()+1900);
		return startDateStr+" - "+endDateStr;
	}
}
