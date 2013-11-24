package org.ucp.ugat.client.common.ui.events;


public class DummySelectionEvent extends ClientEvent<DummySelectionEvent.IDummySelectionEventHandler> {
	public interface IDummySelectionEventHandler extends ClientEvent.IClientEventHandler {
		void handleDummySelectionEvent(int index);
	}
	
	public static final Type<IDummySelectionEventHandler> TYPE=new Type<IDummySelectionEventHandler>();
	private int index;

	@Override
	public Type<IDummySelectionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IDummySelectionEventHandler handler) {
		handler.handleDummySelectionEvent(index);
	}
	
	public DummySelectionEvent(int index) {
		this.index=index;
	}
}