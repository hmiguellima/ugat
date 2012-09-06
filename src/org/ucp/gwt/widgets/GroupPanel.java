package org.ucp.gwt.widgets;

import com.google.gwt.user.client.ui.*;

public class GroupPanel extends Composite {
	TabPanel tab=new TabPanel();
	FlowPanel panel=new FlowPanel();
	FlowPanel holderPanel=new FlowPanel();
	
	public GroupPanel(String htmlTitle) {
		tab.add(panel, htmlTitle, true);
		tab.selectTab(0);
		holderPanel.add(tab);
		initWidget(holderPanel);
		setStyleName("ucpgwt-GroupPanel");
		panel.setStyleName("ucpgwt-GroupPanel-Padding");
	}
	
	public void setGroupCaption(String htmlTitle) {
		tab.clear();
		tab.add(panel, htmlTitle, true);
		tab.selectTab(0);
	}
	
	public void add(Widget w) {
		panel.add(w);
	}
	
	public void clear() {
		panel.clear();
	}
	
	public Widget getWidget(int index) {
		return panel.getWidget(index);
	}
	
	public int getWidgetCount() {
		return panel.getWidgetCount();
	}
	
	public int getWidgetIndex(Widget child) {
		return panel.getWidgetIndex(child);
	}
	
	public boolean remove(int index) {
		return panel.remove(index);
	}
	
}
