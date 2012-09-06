package org.ucp.gwt.util;

public class NumberFormat {
	public static String doubleToString(double value, int decimalPlaces) {
		int parteInteira=new Double(Math.floor(value)).intValue();
		Double parteInteiraDbl=new Double(parteInteira);
		int parteDecimal=new Double(Math.round((value-parteInteiraDbl.doubleValue())*(Math.pow(10, decimalPlaces)))).intValue();
		if (parteDecimal!=0)
			return String.valueOf(parteInteira)+"."+StringUtils.leftPad(String.valueOf(parteDecimal), decimalPlaces, '0');
		else
			return String.valueOf(parteInteira);
	}
}
