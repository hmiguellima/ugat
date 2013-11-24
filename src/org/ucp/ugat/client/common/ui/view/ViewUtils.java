package org.ucp.ugat.client.common.ui.view;

import org.ucp.ugat.client.common.ui.ModalDialog;
import com.google.gwt.user.client.Command;

public class ViewUtils {
	public void alertUser(String message) {
		ModalDialog.showAlertDialog("MyFarm", message);
	}

	public void showConfirmDialog(String title, String message,
			final Command confirmAction) {
		ModalDialog.showConfirmDialog(title, message, new ModalDialog.ModalDialog_ActionHandler() {
			@Override
			public void onAction(int action, Object value) {
				if (action==ModalDialog.ACTION_OK)
					confirmAction.execute();
			}
		});
	}
}
