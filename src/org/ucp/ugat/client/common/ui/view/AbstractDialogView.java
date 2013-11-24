package org.ucp.ugat.client.common.ui.view;

import org.ucp.ugat.client.common.ui.ModalDialog;

import com.google.gwt.user.client.Command;

public abstract class AbstractDialogView extends ModalDialog implements DialogView {
	private ViewUtils viewUtils=new ViewUtils();

	public AbstractDialogView(String title, int width, int height, int buttonStyle) {
		super(title, width, height, buttonStyle);
	}

	public AbstractDialogView(String title, int width, int height) {
		super(title, width, height);
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
