package org.ucp.ugat.client.common.ui.events;

import com.google.gwt.event.shared.*;

public class ClientEventBus {
	private static ClientEventBus instance=new ClientEventBus();
	private HandlerManager manager;
	
	private ClientEventBus() {
		manager=new HandlerManager(this);
	}
	
	public static final ClientEventBus getInstance() {
		return instance; 
	}
	
	public <H extends ClientEvent.IClientEventHandler> void registerHandler(GwtEvent.Type<H> type, final H handler) {
		manager.addHandler(type, handler);
	}
	
	public void fireEvent(ClientEvent event) {
		manager.fireEvent(event);
	}
}
