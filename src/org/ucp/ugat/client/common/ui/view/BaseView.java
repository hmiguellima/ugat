package org.ucp.ugat.client.common.ui.view;

import com.google.gwt.user.client.Command;

public interface BaseView {
	public void alertUser(String message);
	public void showConfirmDialog(String title, String message, Command confirmAction);
	public void show();
}
