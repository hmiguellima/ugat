package org.ucp.gwt.widgets.eventscalendar;

import java.util.*;

public class CalendarEvent extends AbstractCalendarEvent {

	public Object clone() {
		CalendarEvent event=new CalendarEvent();
		event.setDescription(getDescription());
		event.setEnd(getEnd());
		event.setStart(getStart());
		event.setWhat(getWhat());
		event.setOffset(getOffset());
		event.setZOrder(getZOrder());
		event.setWhere(getWhere());
		event.setId(getId());
		return event;
	}

	public void showPopupMenu(int x, int y, HashMap params) {
	}

}
