package org.ucp.gwt.widgets;

import java.util.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

public class EditableCombo extends Composite {
	private PopupPanel choicesPanel;
	private SafeTextBox inputBox=new SafeTextBox();
	private CListBox choicesBox=new CListBox();
	private ImageButton choicesBtn=new ImageButton(GWT.getModuleBaseURL()+"img/dropdownbtn.gif");
	private HorizontalPanel panel=new HorizontalPanel();
	private ClickListenerCollection clickListeners=new ClickListenerCollection();
	private ArrayList<String> choicesList;
	private int maxVisibleItemCount;
	
	public EditableCombo() {
		initWidget(panel);
		setStyleName("ucpgwt-EditableCombo");
		inputBox.setWidth("100%");
		panel.add(inputBox);
		panel.add(choicesBtn);
		panel.setCellWidth(choicesBtn, "14px");
		panel.setCellVerticalAlignment(choicesBtn, HorizontalPanel.ALIGN_MIDDLE);
		choicesPanel=new PopupPanel(false, true);
		choicesPanel.addStyleName("ucpgwt-EditableCombo-ChoicesPanel");
		choicesPanel.add(choicesBox);
		choicesBtn.addClickListener(new EditableCombo_ChoicesBtn_onClickAdapter());
		choicesBox.addClickListener(new EditableCombo_ChoicesBox_onClickAdapter());
	}
	
	public void selectAll() {
		inputBox.selectAll();
	}
	
	public void setFocus(boolean focused) {
		inputBox.setFocus(focused);
	}

	public void addClickListener(ClickListener listener) {
		clickListeners.add(listener);
	}

	public void removeClickListener(ClickListener listener) {
		clickListeners.remove(listener);
	}
	
	public void setChoices(String[] choices, int maxVisibleItemCount) {
		choicesList=new ArrayList<String>();
		for (String choice:choices)
			choicesList.add(choice);
		this.maxVisibleItemCount=maxVisibleItemCount;
	}

	public void setChoices(String[] choices) {
		setChoices(choices, choices.length);
	}
	
	public void addKeyboardListener(KeyboardListener listener) {
		inputBox.addKeyboardListener(listener);
	}
	
	public String getText() {
		return inputBox.getText();
	}

	public void setText(String text) {
		inputBox.setText(text);
	}

	public void setWidth(String width) {
		super.setWidth(width);
		panel.setWidth(width);
	}
	
	private void doClick() {
		int index=choicesBox.getSelectedIndex();
		if (index>=0) {
			choicesPanel.hide();
			inputBox.setText(choicesBox.getItemText(index));
			inputBox.selectAll();
			inputBox.setFocus(true);
			clickListeners.fireClick(EditableCombo.this);
		}
	}
	
	// Event Handlers
	
	private class EditableCombo_ChoicesBox_onClickAdapter implements ClickListener {
		public void onClick(Widget sender) {
			doClick();
		}
	}

	private class EditableCombo_ChoicesBtn_onClickAdapter implements ClickListener {
		public void onClick(Widget sender) {
			if (choicesList.size()>0) {
				choicesBox.clear();
				if ( (inputBox.getText().trim().length()>0) && (choicesList.indexOf(inputBox.getText())<0) )
					choicesBox.addItem(inputBox.getText());
				for (String choice:choicesList)
					choicesBox.addItem(choice);
				if (maxVisibleItemCount<choicesBox.getItemCount())
					choicesBox.setVisibleItemCount(maxVisibleItemCount);
				else
					if (choicesBox.getItemCount()==1)
						choicesBox.setVisibleItemCount(2);
					else
						choicesBox.setVisibleItemCount(choicesBox.getItemCount());
				choicesBox.setSelectedIndex(-1);
				choicesPanel.setPopupPosition(inputBox.getAbsoluteLeft(), inputBox.getAbsoluteTop()+inputBox.getOffsetHeight());
				choicesBox.setWidth(String.valueOf(panel.getOffsetWidth())+"px");
				choicesPanel.show();
			}
		}
	}
}
