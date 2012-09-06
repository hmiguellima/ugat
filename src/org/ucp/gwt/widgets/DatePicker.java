package org.ucp.gwt.widgets;

/*
 * GWT Calendar class built on top of a open source 
 * javascript version.
 * 
 * Provides a popup style calendar associated with a readonly TextBox.
 * Depends on calendar.js
 */

import java.util.*;

import org.ucp.gwt.util.*;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class DatePicker extends TextBox {
	private ChangeListenerCollection changeListeners=new ChangeListenerCollection();
	private int day;
	private boolean isVisible=false;
	private int month;
	private PopupPanel popup;
	private int year;
	
	public DatePicker() {
		this(new Date());
	}
	
	public DatePicker(Date dt) {
		year=dt.getYear()+1900;
		month=dt.getMonth()+1;
		day=dt.getDate();

		setStyleName("ucpgwt-DatePicker");
		DOMUtils.setReadOnly(getElement());
		setText(year+"-"+month+"-"+day);
		addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				if (DatePicker.this.isEnabled())
					showCalendar();
			}
		});
		addKeyboardListener(new KeyboardListenerAdapter() {
			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				if (DatePicker.this.isEnabled())
					showCalendar();
			}
		});
	}
	
	public void setDate(Date dt) {
		year=dt.getYear()+1900;
		month=dt.getMonth()+1;
		day=dt.getDate();

		setText(year+"-"+month+"-"+day);
	}
	
	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}
	
	public Date getDate() {
		return new Date(year-1900, month-1, day, 0, 0, 0);
	}
	
	public void removeChangeListener(ChangeListener listener) {
		changeListeners.remove(listener);
	}
	
	private void showCalendar() {
		if (!isVisible) {
			popup=new PopupPanel(true);
			popup.addPopupListener(new PopupListener() {
				public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
					isVisible=false;
				}
			});
			popup.setStylePrimaryName("ucpgwt-DatePicker-Popup");
			popup.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop()+getOffsetHeight());
			popup.show();
			showDP(year, month, day, popup.getElement(), getElement(), new Command() {
				public void execute() {
					isVisible=false;
					popup.hide();
					String[] dateParts=getText().split("-");
					year=Integer.parseInt(dateParts[0]);
					month=Integer.parseInt(dateParts[1]);
					day=Integer.parseInt(dateParts[2]);
					changeListeners.fireChange(DatePicker.this);
				}
			});
			isVisible=true;
		}
	}
	
	private native void showDP(int year, int month, int day, Element parent, Element inputBox, Command onDateChoiceCallback) /*-{
		var dt=new $wnd.DatePicker(parent);
		function callback(dt) {
			inputBox.value=dt.getFullYear()+'-'+(dt.getMonth()+1)+'-'+dt.getDate();
			dt=null;
			onDateChoiceCallback.@com.google.gwt.user.client.Command::execute()();
		}
		dt.show(new Date(year, month-1, day), callback);
	}-*/;
}
