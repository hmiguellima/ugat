package org.ucp.ugat.client.common.ui.events;

import com.google.gwt.event.shared.*;

public abstract class ClientEvent<H extends ClientEvent.IClientEventHandler> extends GwtEvent<H> {
	public interface IClientEventHandler extends EventHandler {
	}
}
