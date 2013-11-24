package org.ucp.ugat.client.common.ui.view;

import org.ucp.gwt.widgets.*;

import com.google.gwt.user.client.ui.*;

public interface FormView extends BaseView {
	public Widget asWidget();
	public void setMenuItems(CMenuContext menuContext);
	public boolean userConfirms(String message);
}
