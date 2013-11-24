package org.ucp.ugat.client.common.ui.view;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import org.ucp.ugat.client.common.ui.events.DummySelectionEvent;

public class DummySelectionSource implements HasDummySelectionHandlers {
	private HandlerManager manager=new HandlerManager(this);

	@Override
	public void fireEvent(GwtEvent<?> event) {
		manager.fireEvent(event);
	}
	
	public void fireEvent(int index) {
		manager.fireEvent(new DummySelectionEvent(index));
	}

	@Override
	public HandlerRegistration addDummySelectionHandler(DummySelectionEvent.IDummySelectionEventHandler handler) {
		return manager.addHandler(DummySelectionEvent.TYPE, handler);
	}
	
}

