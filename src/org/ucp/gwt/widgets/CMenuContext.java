package org.ucp.gwt.widgets;

/*
 * CMenuContext is part of the CMenu components, providing a list of
 * options (CMenuItem's) associated with a menu context and string key.
 * 
 */

import java.util.*;

import com.google.gwt.user.client.*;

public class CMenuContext {
	private Command cmd;
	private ArrayList itemsList=new ArrayList();
	private HashMap itemsMap=new HashMap();
	private CMenu menu=null;
	private String name;
	
	public CMenuContext(String name, Command cmd) {
		this.name=name;
		this.cmd=cmd;
	}
	
	public CMenuContext(String name) {
		this.name=name;
	}
	
	public void addItem(String key, CMenuItem item) {
		itemsMap.put(key, item);
		itemsList.add(item);
		item.setMenuContext(this);
		menu.buildContextBar();
	}
	
	public void setCmd(Command cmd) {
		this.cmd=cmd;
	}
	
	public Command getCmd() {
		return cmd;
	}

	public CMenuItem getItem(int index) {
		return (CMenuItem)itemsList.get(index);
	}
	
	public CMenuItem getItem(String key) {
		return (CMenuItem)itemsMap.get(key);
	}

	public CMenu getMenu() {
		return menu;
	}

	public String getName() {
		return name;
	}

	public int itemCount() {
		return itemsList.size();
	}

	public void setMenu(CMenu menu) {
		this.menu = menu;
	}
	
	public void hideAllItems() {
		for (Object item:itemsList)
			((CMenuItem)item).setVisible(false);
	}
	
}
