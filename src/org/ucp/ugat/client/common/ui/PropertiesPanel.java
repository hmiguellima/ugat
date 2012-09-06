package org.ucp.ugat.client.common.ui;

import com.google.gwt.user.client.ui.*;

public class PropertiesPanel extends Composite {
	private HorizontalSplitPanel splitPanel=new HorizontalSplitPanel();
	
	public PropertiesPanel() {
		splitPanel.setSplitPosition("300px");
		initWidget(splitPanel);
		addStyleName("ugat-PropertiesPanel");
	}
	
	public void setController(Widget controller) {
		splitPanel.setLeftWidget(controller);
	}
	
	public void clear() {
		splitPanel.setRightWidget(null);
	}

	public void add(Widget details) {
		splitPanel.setRightWidget(details);
	}
}
