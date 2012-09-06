package org.ucp.gwt.widgets;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class PopupMenu extends PopupPanel {
	private MenuBar optionsMenu=new MenuBar(true);
	
	public PopupMenu() {
		super(true);
		setWidget(optionsMenu);
	}
	
	public void addItem(String name, final Command cmd) {
		optionsMenu.addItem(name, new Command() {
			public void execute() {
				hide();
				cmd.execute();
			}
		});
	}
	
	public void popup(int x, int y) {
		setPopupPosition(x, y);
		show();
	}
}
