package org.ucp.gwt.widgets;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class HintedTextBox extends TextBox implements FocusListener, ChangeListener {
	private String hint;
	private boolean empty;
	
	public HintedTextBox(String hint) {
		this.hint=hint;
		addFocusListener(this);
		addChangeListener(this);
		enableHint();
		empty=true;
	}

	public void onFocus(Widget sender) {
		disableHint();
	}

	public void onLostFocus(Widget sender) {
		if (empty)
			enableHint();
	}

	protected void enableHint() {
		DOM.setStyleAttribute(getElement(), "color", "#e0e0e0");
		super.setText(hint);
	}

	protected void disableHint() {
		DOM.setStyleAttribute(getElement(), "color", "");
		if (empty)
			super.setText("");
	}
	
	@Override
	public void setText(String text) {
		empty=(text.length()==0);
		if (empty)
			enableHint();
		else
			disableHint();
	}

	@Override
	public String getText() {
		if (!empty)
			return super.getText();
		else
			return "";
	}

	public void onChange(Widget sender) {
		empty=(super.getText().length()==0);
	}
	
}
