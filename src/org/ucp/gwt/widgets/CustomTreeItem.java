package org.ucp.gwt.widgets;

/*
 * CustomTreeItem is part of the NavTree component.
 * 
 * Adds the following styles to TreeItem:
 * ucpgwt-TreeItem, ucpgwt-TreeItem:hover (see ucpgwt.css).
 */

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class CustomTreeItem extends TreeItem {
	public CustomTreeItem(String nome) {
		super(new FocusableTreeItem(nome));
	}

	public CustomTreeItem(String nome, Object userObject) {
		super(new FocusableTreeItem(nome));
		setUserObject(userObject);
	}
}

class FocusableTreeItem extends SimplePanel implements HasFocus {
	Label label=new Label();
	
	public FocusableTreeItem(String nome) {
		setStyleName("ucpgwt-TreeItem");
		label.setText(nome);
		DOM.setStyleAttribute(label.getElement(), "display", "inline");
		setWidget(label);
	}

	public int getTabIndex() {
		return 0;
	}

	public void setAccessKey(char key) {
	}

	public void setFocus(boolean focused) {
	}

	public void setTabIndex(int index) {
	}

	public void addFocusListener(FocusListener listener) {
	}

	public void removeFocusListener(FocusListener listener) {
	}

	public void addKeyboardListener(KeyboardListener listener) {
	}

	public void removeKeyboardListener(KeyboardListener listener) {
	}
}
