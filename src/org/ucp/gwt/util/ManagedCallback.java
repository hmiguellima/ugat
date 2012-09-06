/**
 * 
 */
package org.ucp.gwt.util;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;

/**
 * @author hcaetano / hlima
 *
 */
public class ManagedCallback<T> implements AsyncCallback<T> {
	private AsyncManager manager;
	private String source;
	private boolean canceled=false;

	public ManagedCallback(AsyncManager manager, String source) throws Exception {
		this.manager=manager;
		this.source=source;
		manager.addCallback();
	}

	public ManagedCallback(String source) throws Exception {
		this(new AsyncManager(), source);
		manager.prepare();
	}
	
	public final void onSuccess(final T result) {
		if (!canceled)
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					ManagedCallback.this.execute(result);
				}
			});
		manager.notifySuccess();
	}

	public final void onFailure(Throwable caught) {
		manager.notifyFailure(caught, source);
	}

	public void execute(T result) {
	}
	
	public final void cancel() {
		canceled=true;
	}
}
