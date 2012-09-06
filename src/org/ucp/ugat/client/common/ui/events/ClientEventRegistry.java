package org.ucp.ugat.client.common.ui.events;

import java.util.*;

import org.ucp.ugat.client.common.ui.events.ClientEvent.*;

import com.google.gwt.event.shared.*;

public class ClientEventRegistry {
	private static final ClientEventRegistry instance=new ClientEventRegistry();
	private HashMap<String,GwtEvent.Type<IClientEventHandler>> map=new HashMap<String,GwtEvent.Type<IClientEventHandler>>(); 
	
	private ClientEventRegistry() {
	}

	public static ClientEventRegistry getInstance() {
		return instance;
	}
	
	public void registerEventType(String key, GwtEvent.Type<IClientEventHandler> type) {
		map.put(key, type);
	}
	
	public GwtEvent.Type<IClientEventHandler> getEventType(String key) {
		return map.get(key);
	}
}
