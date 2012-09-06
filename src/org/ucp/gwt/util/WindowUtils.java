package org.ucp.gwt.util;

public class WindowUtils {
	public static native int getClientHeight() /*-{
		return $doc.documentElement.clientHeight;
	}-*/;
	
	public static native int getScrollLeft() /*-{
	    var scrollLeft;
	    if ($wnd.innerHeight)
	    {
	            scrollLeft = $wnd.pageXOffset;
	    }
	    else if ($doc.documentElement && $doc.documentElement.scrollLeft)
	    {
	            scrollLeft = $doc.documentElement.scrollLeft;
	    }
	    else if ($doc.body)
	    {
	            scrollLeft = $doc.body.scrollLeft;
	    }
	    return scrollLeft;
    }-*/; 	

	public static native int getScrollTop() /*-{
	    var scrollTop;
	    if ($wnd.innerHeight)
	    {
	            scrollTop = $wnd.pageYOffset;
	    }
	    else if ($doc.documentElement && $doc.documentElement.scrollTop)
	    {
	            scrollTop = $doc.documentElement.scrollTop;
	    }
	    else if ($doc.body)
	    {
	            scrollTop = $doc.body.scrollTop;
	    }
	    return scrollTop;
    }-*/;
	
	public static native void openDataURI(String mime, String data) /*-{
	    var dataURI;
	    
	    dataURI="data:"+mime+";charset=utf-8,"+encodeURIComponent(data);
	    window.open(dataURI);
	}-*/;
}
