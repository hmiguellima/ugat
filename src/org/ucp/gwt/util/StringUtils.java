package org.ucp.gwt.util;

public class StringUtils {
	public static String leftPad(String str, int len, char padding) {
		String result;
		result=str;
		while (result.length()<len)
			result=padding+result;
		return result;
	}

	public static String rightPad(String str, int len, char padding) {
		String result;
		result=str;
		while (result.length()<len)
			result=result+padding;
		return result;
	}
	
	public static String shortTimeFormat(int hour, int minute) {
		return leftPad(String.valueOf(hour), 2, '0')+":"+leftPad(String.valueOf(minute), 2, '0');
	}
}
