package org.ucp.gwt.widgets;

import org.ucp.gwt.util.*;

import com.google.gwt.user.client.ui.*;

public class CMenuURLItem extends CMenuItem {
	public CMenuURLItem(String name, String url, boolean visible) {
		super(name, new DummyCommand(), visible);
		try {
			setHTML("<a class='ucpgwt-CMenuURLItem' href=\""+url+"\">"+name+"</a>");
		} catch (Exception e) {}
	}

	public MenuItem getPopupMenuItem() {
		MenuItem item=null;
		
		try {
			item=new MenuItem(getHTML(), true, getCmd());
		} catch (Exception e) {}
		return item;
	}
}
