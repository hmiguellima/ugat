package org.ucp.gwt.widgets;

import com.google.gwt.user.client.ui.*;

public class StandbyPanel {
	private static HTML sDiv=null;
	private static int pendingActions=0;
	
	public static void show() {
		pendingActions++;
		
		if (sDiv!=null) 
			return;
		
		StringBuffer html=new StringBuffer();
		
	    html.append("<div class='ucpgwt-StandbyPanel-Msg'>");
	    html.append("Aguarde...&nbsp;<img style='vertical-align:middle' src='img/mozilla_blu.gif'></img>");
	    html.append("</div>");
	    html.append("<div class='ucpgwt-StandbyPanel'>");
	    html.append("</div>");
	    
	    sDiv=new HTML(html.toString());
	    RootPanel.get().add(sDiv);
	}

	public static void hide() {
		if (pendingActions>0)
			pendingActions--;
		if ( (sDiv!=null) && (pendingActions==0) ) {
			RootPanel.get().remove(sDiv);
			sDiv=null;
		}
	}
}
