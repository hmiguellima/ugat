package org.ucp.ugat.client.common.ui;

import java.util.*;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.*;

public class FileUploadDialog  extends ModalDialog implements ModalDialog.ModalDialog_ActionListener, FormPanel.SubmitCompleteHandler {
	FlowPanel panel=new FlowPanel();
	FlowPanel hiddenFields=new FlowPanel();
	FormPanel form;
	FileUpload upload=new FileUpload();
	FormPanel.SubmitCompleteHandler submitHandler;
	Command submitCommand;
	
	private FileUploadDialog(String title, String uploadFieldName, String action, FormPanel.SubmitCompleteHandler submitHandler, Command submitCommand) {
		super(title, 400, 110);

		this.submitHandler=submitHandler;
		this.submitCommand=submitCommand;
		setHideOnAceitar(false);
		DOM.setElementProperty(upload.getElement(), "size", "44");
		panel.add(upload);
		panel.add(hiddenFields);
		form=new FormPanel();
		form.setWidget(panel);
		setWidget(form);

		form.setAction(action);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);
	    form.addSubmitCompleteHandler(this);
		upload.setName(uploadFieldName);
		addActionListener(this);
	}

	public FileUploadDialog(String title, String uploadFieldName, String action, Command submitCommand) {
		this(title, uploadFieldName, action, null, submitCommand);
	}

	public FileUploadDialog(String title, String uploadFieldName, String action, FormPanel.SubmitCompleteHandler submitHandler) {
		this(title, uploadFieldName, action, submitHandler, null);
	}
	
	public void addHiddenFields(HashMap<String,String> paramMap) {
		for (String key:paramMap.keySet())
			hiddenFields.add(new Hidden(key, paramMap.get(key)));
	}
	
	protected String getFileName() {
		return upload.getFilename();
	}

	public void aceitarAction(Object result) {
		form.submit();
	}

	public void cancelarAction() {
	}

	public void onSubmitComplete(SubmitCompleteEvent event) {
		hide();
		if (submitHandler!=null)
			submitHandler.onSubmitComplete(event);
		if (submitCommand!=null)
			submitCommand.execute();
	}
}
