package org.ucp.ugat.client.common.ui.view;

import org.ucp.gwt.widgets.*;
import org.ucp.ugat.client.common.ui.*;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public abstract class AbstractFormView extends Form implements FormView {
	private ViewUtils viewUtils=new ViewUtils();
	
	public AbstractFormView(String formKey, CMenuContext menuContext) {
		this(formKey);
		setMenuContext(menuContext);
		setMenuItems(menuContext);
	}
	
	private AbstractFormView(String key) {
		super(key);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void show() {
		execute();
	}

	@Override
	protected final void init() {
	}

	public boolean userConfirms(String message) {
		return Window.confirm(message);
	}

	@Override
	public void alertUser(String message) {
		viewUtils.alertUser(message);
	}

	@Override
	public void showConfirmDialog(String title, String message,
			Command confirmAction) {
		viewUtils.showConfirmDialog(title, message, confirmAction);
	}
	
}
