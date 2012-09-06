package org.ucp.gwt.widgets;

import com.google.gwt.user.client.ui.*;

public class BalloonDecorator extends Composite implements FocusListener {
	Balloon balloon=null;
	HTML html=new HTML();
	int width, height;
	
	public BalloonDecorator(Widget widget, int width, int height, String htmlStr) {
		initWidget(widget);
		html.setHTML(htmlStr);
		this.width=width;
		this.height=height;
		((HasFocus)widget).addFocusListener(this);
	}

	public void onFocus(Widget sender) {
		balloon=Balloon.make(sender, width, height, true);
		balloon.draw(html);
	}

	public void onLostFocus(Widget sender) {
		balloon.timedHide(250);
	}
}
