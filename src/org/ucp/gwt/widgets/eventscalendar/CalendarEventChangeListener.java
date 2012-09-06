package org.ucp.gwt.widgets.eventscalendar;

public interface CalendarEventChangeListener {
	public void onPrevMonth();
	public void onNextMonth();
	public void onEventClick(AbstractCalendarEvent event, int x, int y);
}
