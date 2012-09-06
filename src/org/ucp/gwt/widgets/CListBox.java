package org.ucp.gwt.widgets;

/*
 * Custom ListBox based on the standard GWT ListBox.
 * It's primary purpuse was to improve some of the base features and
 * correct some bugs of the original GWT version.
 * 
 * Provides an ArrayList of objects associated with each item in the
 * listBox.
 */

import java.util.*;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class CListBox extends Composite {
	private class CListBox_ListBox extends ListBox {
		public CListBox_ListBox() {
			sinkEvents(Event.ONBLUR | Event.ONCLICK | Event.ONKEYPRESS);
		}
		
		public void onBrowserEvent(Event event) {
			super.onBrowserEvent(event);
		    switch (DOM.eventGetType(event)) {
			    case Event.ONBLUR:
			    	focusListeners.fireLostFocus(this);
			    	break;
			    case Event.ONCLICK:
			    	clickListeners.fireClick(this);
			    	break;
			    case Event.ONKEYPRESS:
			    	keyboardListeners.fireKeyPress(this, (char)DOM.eventGetKeyCode(event), 0);
			    	break;
		    }
		}
	}
	
	private CListBox_ListBox listBox;
	private ClickListenerCollection clickListeners=new ClickListenerCollection();
	private FocusListenerCollection focusListeners=new FocusListenerCollection();
	private KeyboardListenerCollection keyboardListeners=new KeyboardListenerCollection();
	private ArrayList userObjectsList=new ArrayList();
	
	public CListBox() {
		listBox=new CListBox_ListBox();
		initWidget(listBox);
	}
	
	public void addClickListener(ClickListener listener) {
		clickListeners.add(listener);
	}

	public void addFocusListener(FocusListener listener) {
		focusListeners.add(listener);
	}
	
	public void addItem(String item) {
		addItem(item, null);
	}
	
	public void addItem(String item, Object userObject) {
		listBox.addItem(item);
		userObjectsList.add(userObject);
	}

	public int getSelectedIndex() {
		return listBox.getSelectedIndex();
	}
	
	public void setVisibleItemCount(int visibleItems) {
		listBox.setVisibleItemCount(visibleItems);
	}
	
	public void addChangeListener(ChangeListener listener) {
		listBox.addChangeListener(listener);
	}
	
	public void setSelectedIndex(int index){
		listBox.setSelectedIndex(index);
	}

	public String getItemText(int index) {
		return listBox.getItemText(index);
	}
	
	public int getItemCount() {
		return listBox.getItemCount();
	}
	
	public void setFocus(boolean focused) {
		listBox.setFocus(focused);
	}
	
	public void setEnabled(boolean enabled) {
		listBox.setEnabled(enabled);
	}
	
	public void addKeyboardListener(KeyboardListener listener) {
		keyboardListeners.add(listener);
	}

	public void clear() {
		listBox.clear();
		userObjectsList.clear();
	}

	public Object getUserObject(int index) {
		return userObjectsList.get(index);
	}

	public void removeItem(int index) {
		listBox.removeItem(index);
		userObjectsList.remove(index);
	}
	
	public void removeClickListener(ClickListener listener) {
		clickListeners.remove(listener);
	}

	public void removeFocusListener(FocusListener listener) {
		focusListeners.remove(listener);
	}
	
	public void removeKeyboardListener(KeyboardListener listener) {
		keyboardListeners.remove(listener);
	}
	
	public void setSelectedObject(Object obj) {
		Object listObj;
		
		for (int index=0;index<userObjectsList.size();index++) {
			listObj=userObjectsList.get(index);
			if (listObj!=null)
				if (listObj.equals(obj)) {
					listBox.setSelectedIndex(index);
					break;
				}
		}
	}
	
	public boolean isEnabled() {
		return listBox.isEnabled();
	}
}
