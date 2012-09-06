package org.ucp.gwt.util;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;

public class CallbackAdapter<T> implements AsyncCallback<T> {
	public void onFailure(Throwable caught) {
		Window.alert(caught.getMessage());
	}

	public void onSuccess(T result) {
	}
}
