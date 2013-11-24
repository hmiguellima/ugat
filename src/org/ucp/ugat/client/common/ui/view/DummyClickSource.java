package org.ucp.ugat.client.common.ui.view;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import org.ucp.ugat.client.common.ui.events.DummyClickEvent;

public class DummyClickSource implements HasClickHandlers {
	HandlerManager manager=new HandlerManager(this);
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		manager.fireEvent(event);
	}

	public void fireEvent() {
		manager.fireEvent(DummyClickEvent.getInstance());
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return manager.addHandler(DummyClickEvent.getInstance().getAssociatedType(), handler);
	}
}
