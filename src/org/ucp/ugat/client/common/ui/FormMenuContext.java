package org.ucp.ugat.client.common.ui;

import org.ucp.gwt.widgets.*;

public class FormMenuContext extends CMenuContext {

	public FormMenuContext(String name, Form form) {
		super(name, form);
		form.setMenuContext(this);
	}
}
