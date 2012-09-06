package org.ucp.gwt.widgets;

/*
 * CMenuPopup is part of the CMenu components, providing a popup list
 * of non-visible options in the main menu's bar.
 * It's basically a wrapper around GWT's MenuBar.
 */

import com.google.gwt.user.client.ui.*;

public class CMenuPopup extends MenuBar {
	
	public CMenuPopup() {
		this(true);
	}

	public CMenuPopup(boolean vertical) {
		super(vertical);
		setStyleName("ucpgwt-CMenuPopup");
		setAnimationEnabled(true);
	}
	
}
