package org.ucp.gwt.widgets.eventscalendar;

import java.util.*;
import org.ucp.gwt.util.*;
import com.google.gwt.json.client.*;

public abstract class AbstractCalendarEvent implements Comparable {
	private String description="";
	private CalendarEventWhen end=null;
	private CalendarEventWhen start=null;
	private String what="";
	private String where="";
	private String id="";
	private int zOrder=0;
	private int offset=0;
	private String backColorOverride;
	private String headerBackColorOverride;
	
	public AbstractCalendarEvent() {
		backColorOverride=null;
		headerBackColorOverride=null;
	}
	
	public AbstractCalendarEvent(JSONObject json) {
		this();
		setWhat(json.get("what").isString().stringValue());
		setDescription(json.get("description").isString().stringValue());
		setWhere(json.get("where").isString().stringValue());
		setStart(new CalendarEventWhen(json.get("start").isObject()));
		setEnd(new CalendarEventWhen(json.get("end").isObject()));
		setId(json.get("id").isString().stringValue());
	}
	
	public String getDescription() {
		return description;
	}
	public CalendarEventWhen getEnd() {
		return end;
	}
	public CalendarEventWhen getStart() {
		return start;
	}
	public String getWhat() {
		return what;
	}
	public String getWhere() {
		return where;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setEnd(CalendarEventWhen end) {
		this.end = end;
	}
	public void setStart(CalendarEventWhen start) {
		this.start = start;
	}
	public void setWhat(String what) {
		this.what = what;
	}
	public void setWhere(String where) {
		this.where = where;
	}

	public int getZOrder() {
		return zOrder;
	}

	public void setZOrder(int order) {
		zOrder = order;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public abstract Object clone();
	/*{
		CalendarEvent event=new CalendarEvent();
		event.setDescription(description);
		event.setEnd(end);
		event.setStart(start);
		event.setWhat(what);
		event.setOffset(offset);
		event.setZOrder(zOrder);
		event.setWhere(where);
		event.setId(id);
		return event;
	}*/

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getTip() {
		StringBuffer tipBuilder=new StringBuffer();
		String[] descLines;

		tipBuilder.append("Descrição:\n");
		descLines=getDescription().split("\n");
		for (int index=0;index<descLines.length;index++)
			tipBuilder.append("  "+descLines[index]+"\n");
		tipBuilder.append("\nOnde:\n");
		tipBuilder.append("  "+getWhere());
		
		return tipBuilder.toString();
	}
	
	public String getStartShortTime() {
		return StringUtils.shortTimeFormat(getStart().getHour(), getStart().getMinute());
	}

	public String getEndShortTime() {
		return StringUtils.shortTimeFormat(getEnd().getHour(), getEnd().getMinute());
	}
	
	public String getTitle() {
		return "("+getStartShortTime()+"-"+getEndShortTime()+") "+getWhat();
	}

	public String getBackColorOverride() {
		return backColorOverride;
	}

	public void setBackColorOverride(String backColorOverride) {
		this.backColorOverride = backColorOverride;
	}

	public String getHeaderBackColorOverride() {
		return headerBackColorOverride;
	}

	public void setHeaderBackColorOverride(String headerBackColorOverride) {
		this.headerBackColorOverride = headerBackColorOverride;
	}

	public int compareTo(Object obj) {
		if (start.getDate().after(((AbstractCalendarEvent)obj).getStart().getDate()))
			return 1;
		else
			return -1;
	}
	
	
	public abstract void showPopupMenu(int x, int y, HashMap params);
}
