package org.ucp.gwt.util;

import com.google.gwt.user.client.*;

public class DOMUtils {
	public static native void setReadOnly(Element el) /*-{
		el['readOnly']=true;
    }-*/;

	public static native int getOffsetHeight(Element el) /*-{
	  return el.offsetHeight;
	}-*/;

	public static native int getOffsetWidth(Element el) /*-{
	  return el.offsetWidth;
	}-*/;
}
