package org.ucp.ugat.client.common.ui.view;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import org.ucp.ugat.client.common.ui.events.DummySelectionEvent;

public interface HasDummySelectionHandlers extends HasHandlers {
	  HandlerRegistration addDummySelectionHandler(DummySelectionEvent.IDummySelectionEventHandler handler);
}
