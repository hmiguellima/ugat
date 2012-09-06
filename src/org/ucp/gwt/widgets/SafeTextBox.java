package org.ucp.gwt.widgets;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class SafeTextBox extends Composite implements HasFocus {
	class SafeTextBox_ChangeListener implements ChangeListener {
		public void onChange(Widget sender) {
			TextBoxBase box=(TextBoxBase)sender;
			
			if (box.getText().indexOf('\'')!=-1) {
				Window.alert("Este campo não pode conter \'");
				box.setText(box.getText().replaceAll("\'", ""));
			}

			if (box.getText().indexOf('\\')!=-1) {
				Window.alert("Este campo não pode conter \\");
				box.setText(box.getText().replaceAll("\\\\", ""));
			}
		}
	}

	class SafeTextBox_TextArea extends TextArea {
		public SafeTextBox_TextArea() {
			DOM.sinkEvents(getElement(), Event.KEYEVENTS | Event.MOUSEEVENTS | Event.FOCUSEVENTS | Event.ONCHANGE);
		}
		
		@Override
		public void onBrowserEvent(Event event) {
			if (validateKeyEvent(true, event))
				super.onBrowserEvent(event);
		}
	}
	
	class SafeTextBox_TextBox extends TextBox {
		public SafeTextBox_TextBox() {
			DOM.sinkEvents(getElement(), Event.KEYEVENTS | Event.MOUSEEVENTS | Event.FOCUSEVENTS | Event.ONCHANGE);
		}
		
		@Override
		public void onBrowserEvent(Event event) {
			if (validateKeyEvent(false, event))
				super.onBrowserEvent(event);
		}
	}

	private TextBoxBase txtBox;
	
	public SafeTextBox() {
		this(1);
	}
	
	public SafeTextBox(int visibleLines) {
		if (visibleLines==1)
			txtBox=new SafeTextBox_TextBox();
		else {
			txtBox=new SafeTextBox_TextArea();
			((SafeTextBox_TextArea)txtBox).setVisibleLines(visibleLines);
		}
		txtBox.addChangeListener(new SafeTextBox_ChangeListener());
		initWidget(txtBox);
	}
	
	public void addClickListener(ClickListener listener) {
		txtBox.addClickListener(listener);
	}
	
	public void addFocusListener(FocusListener listener) {
		txtBox.addFocusListener(listener);
	}
	
	public void addKeyboardListener(KeyboardListener listener) {
		txtBox.addKeyboardListener(listener);
	}
	
	public int getTabIndex() {
		return txtBox.getTabIndex();
	}
	
	public String getText() {
		return txtBox.getText();
	}
	
	public void removeClickListener(ClickListener listener) {
		txtBox.removeClickListener(listener);
	}
	
	public void removeFocusListener(FocusListener listener) {
		txtBox.removeFocusListener(listener);
	}
	
	public void removeKeyboardListener(KeyboardListener listener) {
		txtBox.removeKeyboardListener(listener);
	}
	
	public void selectAll() {
		txtBox.selectAll();
	}
	
	public void setAccessKey(char key) {
		txtBox.setAccessKey(key);
	}
	
	public void setEnabled(boolean isEnabled) {
		txtBox.setEnabled(isEnabled);
	}

	public void setFocus(boolean isFocused) {
		txtBox.setFocus(isFocused);
	}

	public void setMaxLength(int charCount) {
		if (txtBox instanceof TextBox)
			((TextBox)txtBox).setMaxLength(charCount);
	}

	public void setTabIndex(int index) {
		txtBox.setTabIndex(index);
	}

	public void setText(String text) {
		txtBox.setText(text);
	}

	public void setVisibleLength(int charCount) {
		if (txtBox instanceof TextBox)
			((TextBox)txtBox).setVisibleLength(charCount);
		else
			((TextArea)txtBox).setCharacterWidth(charCount);
	}

	private boolean validateKeyEvent(boolean isTextArea, Event event) {
		int evtType = DOM.eventGetType(event);
		int keyCode;
		boolean shiftPressed;

        if (evtType == Event.ONKEYDOWN)  {
        	shiftPressed = DOM.eventGetShiftKey(event);
            keyCode = DOM.eventGetKeyCode(event);

            if ( ( (keyCode==219) && !shiftPressed) ||
	        	 ( (keyCode==220) && !shiftPressed) ) {
	        	DOM.eventGetCurrentEvent().preventDefault();
	        	return false;
	        }
        } else
	        if (evtType == Event.ONKEYPRESS)
	            if (isTextArea && (DOM.eventGetKeyCode(event)==KeyCodes.KEY_ENTER) )
	            	event.stopPropagation();
        
        return true;
    }
}
