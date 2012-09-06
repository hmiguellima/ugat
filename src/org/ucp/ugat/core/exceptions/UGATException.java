package org.ucp.ugat.core.exceptions;

public class UGATException extends Exception {
	private static final long serialVersionUID = -5771090216743251420L;
	private String messageStr="";
	private String source="";

	public void setMessageStr(String messageStr) {
       this.messageStr=messageStr; 		
	}

	public void setSource(String source) {
		this.source=source;
	}

	public String getMessage() {
		return source+":"+messageStr;
	}

	public String getSource() {
		return source;
	}
}
