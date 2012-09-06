package org.ucp.gwt.widgets;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class CMenuItem extends Composite {
	private class CustomHTML extends HTML {
		public CustomHTML() {
			sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT);
		}
		
		public void onBrowserEvent(Event event) {
			switch (DOM.eventGetType(event)) {
				case Event.ONMOUSEOVER: {
					setStyleName("ucpgwt-CMenuItem-Selected");
					break;
				}
				
				case Event.ONMOUSEOUT: {
					setStyleName("ucpgwt-CMenuItem");
					break;
				}
				
				case Event.ONCLICK: {
					cmd.execute();
				}
			}
		}
	}
	
	private String name;
	private Command cmd=null;
	private CMenuContext menuContext=null;
	private CustomHTML htmlPanel=null;
	
	public CMenuItem() {
	}
	
	public CMenuItem(String name, String imgSrc, Command cmd, boolean visible) {
		String html;
		htmlPanel=new CustomHTML();
		
		setCmd(cmd);
		this.name=name.replaceAll(" ", "&nbsp;");
		
		if (imgSrc!=null)
			html="<img src='"+imgSrc+"'></img>&nbsp;"+name+"&nbsp;";
		else 
			html="&nbsp;"+name+"&nbsp;";
		
		htmlPanel.setHTML(html);
		initWidget(htmlPanel);
		setStyleName("ucpgwt-CMenuItem");
		setVisible(visible);
	}

	public CMenuItem(String name, Command cmd, boolean visible) {
		this(name, null, cmd, visible);
	}

	public CMenuItem(String name, String imgSrc, Command cmd) {
		this(name, imgSrc, cmd, true);
	}

	public CMenuItem(String name, Command cmd) {
		this(name, null, cmd, true);
	}
	
	public MenuItem getPopupMenuItem() {
		MenuItem item=new MenuItem(name, true, cmd);
		return item;
	}
	
	public void setHTML(String html) throws Exception {
		if (htmlPanel!=null) 
			htmlPanel.setHTML(html);
		else
			throw new Exception("CMenuItem::setHTML:Method not allowed");
	}

	public String getHTML() throws Exception {
		if (htmlPanel!=null) 
			return htmlPanel.getHTML();
		else
			throw new Exception("CMenuItem::setHTML:Method not allowed");
	}

	public Command getCmd() {
		return cmd;
	}

	public void setCmd(Command cmd) {
		this.cmd = cmd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CMenuContext getMenuContext() {
		return menuContext;
	}

	public void setMenuContext(CMenuContext menuContext) {
		this.menuContext = menuContext;
	}
}
