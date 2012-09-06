package org.ucp.gwt.widgets;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

@SuppressWarnings("deprecation")
public class NumberBox extends TextBox implements KeyPressHandler {
	private static final char[] allowedChars={KeyboardListener.KEY_BACKSPACE, KeyboardListener.KEY_DELETE, KeyboardListener.KEY_ESCAPE, KeyboardListener.KEY_LEFT, KeyboardListener.KEY_RIGHT, KeyboardListener.KEY_TAB};
	private boolean allowDecimalPlaces;
	private boolean allowNegatives;
	
	public NumberBox(boolean allowDecimalPlaces, boolean allowNegatives) {
		addKeyPressHandler(this);
		this.allowDecimalPlaces=allowDecimalPlaces;
		this.allowNegatives=allowNegatives;
	}
	
	public long getLongValue() {
		return Long.parseLong(getText());
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		TextBox box=this;
		char keyCode=event.getCharCode();
		char nativeKeyCode=(char)event.getNativeEvent().getKeyCode();
		
		// 1º vamos tratar o ponto e o menos
		if ( (keyCode=='.') || (keyCode=='-') ) {
			if (keyCode=='.') {
				if (!allowDecimalPlaces) {
					box.cancelKey();
					return;
				}
				if ( (box.getText().indexOf('.')!=-1) || (box.getText().length()==0) ) {
					box.cancelKey();
					return;
				}
			} else {
				if (!allowNegatives) {
					box.cancelKey();
					return;
				}
				if (box.getText().length()>0) {
					box.cancelKey();
					return;
				}
			}
			return;
		}
		
		// Se for um digito numérico pode ser
		if ( (keyCode >= '0') && (keyCode <= '9') ) 
			return;

		// Tambem pode ser escape, backspace, delete, esq, dir, tab
		for (int conta=0;conta<allowedChars.length;conta++)
			if (nativeKeyCode==allowedChars[conta]) 
				return;

		// Caso contrário, não pode ser
		box.cancelKey();
	}
}
