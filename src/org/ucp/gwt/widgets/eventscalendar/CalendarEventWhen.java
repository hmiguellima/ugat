package org.ucp.gwt.widgets.eventscalendar;

import java.util.*;

import com.google.gwt.json.client.*;

public class CalendarEventWhen {
	private int day;
	private int hour;
	private int minute;
	private int month;
	private int year;

	public CalendarEventWhen() {
	}
	
	public CalendarEventWhen(Date date) {
		setDay(date.getDate());
		setMonth(date.getMonth()+1);
		setYear(date.getYear()+1900);
		setHour(date.getHours());
		setMinute(date.getMinutes());
	}
	
	public CalendarEventWhen(JSONObject json) {
		setDay((int)(json.get("day").isNumber().getValue()));
		setMonth((int)(json.get("month").isNumber().getValue()));
		setYear((int)(json.get("year").isNumber().getValue()));
		setHour((int)(json.get("hour").isNumber().getValue()));
		setMinute((int)(json.get("minute").isNumber().getValue()));
	}

	public int getDay() {
		return day;
	}
	public int getHour() {
		return hour;
	}
	public int getMinute() {
		return minute;
	}
	public int getMonth() {
		return month;
	}
	public int getYear() {
		return year;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	public Date getDate() {
		return new Date(getYear()-1900, getMonth()-1, getDay(), getHour(), getMinute(), 0);
	}
}
