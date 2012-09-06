package org.ucp.ugat.client.plugins;

import org.ucp.gwt.widgets.*;
import org.ucp.ugat.client.common.*;
import org.ucp.ugat.client.common.ui.events.*;
import org.ucp.ugat.client.entry.*;

import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.*;


public class PluginApiService {
	private static PluginApiService instance=new PluginApiService();

	private PluginApiService() {
	}

	public static PluginApiService getInstance() {
		return instance;
	}
	
	public <H extends ClientEvent.IClientEventHandler> void registerHandler(GwtEvent.Type<H> type, final H handler) {
		ClientEventBus.getInstance().registerHandler(type, handler);
	}
	
	public CMenuItem addMenuItem(String menuKey, String contextKey, String itemKey, String itemLabel, boolean itemVisible, final Command cmd) {
		CMenuItem item;

		item=new CMenuItem(itemLabel, cmd, itemVisible);
		CMenuManager.getMenu(menuKey).getContext(contextKey).addItem(itemKey, item);
		return item;
	}
	
	public int getModuleId() {
		return AppEntry.getCurrentModule().getModuleId(); 
	}
	
	public void registerPlugin(Plugin plugin) {
		PluginRegistry.getInstance().register(plugin);
	}
}

