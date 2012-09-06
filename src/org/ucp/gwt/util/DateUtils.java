package org.ucp.gwt.util;

import java.util.*;

public class DateUtils {
	@SuppressWarnings("deprecation")
	public static String dateTimeToStr(Date dt) {
		String str;

		if (dt==null) return null;
		str=String.valueOf(dt.getYear()+1900);
		str+="/"+String.valueOf(dt.getMonth()+1);
		str+="/"+String.valueOf(dt.getDate());
		str+=" "+String.valueOf(dt.getHours());
		str+=":"+String.valueOf(dt.getMinutes());
		
		return str;
	}

	@SuppressWarnings("deprecation")
	public static String dateToStr(Date dt) {
		String str;

		if (dt==null) return null;
		str=String.valueOf(dt.getYear()+1900);
		str+="/"+String.valueOf(dt.getMonth()+1);
		str+="/"+String.valueOf(dt.getDate());
		
		return str;
	}

	@SuppressWarnings("deprecation")
	public static Date strToDate(String str) {
		String[] dateTimeParts;
		String[] dateParts;
		
		dateTimeParts=str.split(" ");
		dateParts=dateTimeParts[0].split("-");
		
		return new Date(Integer.parseInt(dateParts[0])-1900, Integer.parseInt(dateParts[1])-1, Integer.parseInt(dateParts[2]));
	}
}
