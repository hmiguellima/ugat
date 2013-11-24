package org.ucp.ugat.client.common.ui.events;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;

public class DummyClickEvent extends GwtEvent<ClickHandler> {
	private static GwtEvent.Type<ClickHandler> TYPE=new GwtEvent.Type<ClickHandler>(); 
	private static DummyClickEvent instance=new DummyClickEvent();
	
	private DummyClickEvent() {
	}
	
	public static DummyClickEvent getInstance() {
		return instance;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ClickHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ClickHandler handler) {
		handler.onClick(null);
	}
}