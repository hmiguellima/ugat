package org.ucp.gwt.widgets;

/*
 * CMenuManager is part of the CMenu components, and provides an easy way
 * of managing multiple menus.
 * 
 * Provides static methods to register, retrieve, enable and disable
 * the application menu's.
 */

import java.util.*;

import com.google.gwt.user.client.ui.*;

public class CMenuManager {
	private static HashMap menus=new HashMap();
	
	public static void addMenu(String key, CMenu menu) {
		menus.put(key, menu);
	}
	
	public static void disableMenus() {
		for (int conta=0;conta<menus.size();conta++) {
			((CMenu)menus.values().toArray()[conta]).disable();
		}
	}
	
	public static void enableMenus() {
		for (int conta=0;conta<menus.size();conta++) {
			((CMenu)menus.values().toArray()[conta]).enable();
		}
	}
	
	public static CMenu getMenu(String key) {
		return (CMenu)menus.get(key);
	}
	
	public static void init() throws Exception {
		try {
			for (int conta=0;conta<menus.size();conta++) {
				RootPanel.get("controller").add(((CMenu)menus.values().toArray()[conta]));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
}
