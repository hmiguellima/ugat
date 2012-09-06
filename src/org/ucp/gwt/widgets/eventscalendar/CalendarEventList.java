package org.ucp.gwt.widgets.eventscalendar;

import java.util.*;

public class CalendarEventList extends ArrayList {
	private static final long serialVersionUID = -5334826395331151920L;

	public AbstractCalendarEvent getEvent(int index) {
		return (AbstractCalendarEvent)get(index);
	}
}
