package org.ucp.ugat.client.common.ui;

import java.util.*;

import org.ucp.gwt.widgets.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public abstract class ModalDialog implements KeyPressHandler {
	class CDialog_aceitarBtn_onClickAdapter implements ClickListener {
		public void onClick(Widget sender) {
			try {
				modalResult=evalModalResult();
				if (hideOnAceitar)
					DeferredCommand.addCommand(new Command() {
						public void execute() {
							close();
						}
					});
					
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						onAceitar();
					}
				});
			} catch (Exception ex) {
				com.google.gwt.user.client.Window.alert(ex.getMessage());
			}
		}
	}
	
	class CDialog_closeButton_onClickAdapter implements ClickListener {
		public void onClick(Widget sender) {
			close();
			onCancelar();
		}
	}
	
	public interface ModalDialog_ActionHandler {
		public void onAction(int action, Object value);
	}
	
	public interface ModalDialog_ActionListener {
		public void aceitarAction(Object result);
		public void cancelarAction();
	}
	
	public static final int BUTTONS_CANCEL=2;
	public static final int BUTTONS_NONE=0;
	public static final int BUTTONS_OK=1;
    public static final int BUTTONS_OK_CANCEL=3;
    
    public static final int ACTION_OK=0;
    public static final int ACTION_CANCEL=1;
    
	private Button aceitarBtn=new Button("Aceitar");
	private ArrayList<ModalDialog_ActionListener> actionListeners=new ArrayList<ModalDialog_ActionListener>();
	private FlowPanel borderInnerPanel=new FlowPanel();
	private Button cancelarBtn=new Button("Cancelar");
	private FlowPanel centerPanel=new FlowPanel();
	private Widget child;
	private DialogBox dialogImpl;
	private ArrayList footerBtnList=new ArrayList();
	private FlowPanel footerPanel=new FlowPanel();
	private boolean hideOnAceitar=true;
	private Button defaultBtn=null;
	private FocusPanel focusPanel=new FocusPanel();
	private FlowPanel lightBox=new FlowPanel();
	private Object modalResult=null;
	
	public ModalDialog(String title, int width, int height) {
		this(title, width, height, BUTTONS_OK_CANCEL);
	}
	
	public ModalDialog(String title, int width, int height, int buttonStyle) {
		dialogImpl=new DialogBox();
		dialogImpl.setText(title);
		dialogImpl.setAnimationEnabled(true);
        
        lightBox.setStyleName("ugat-ModalDialog-LightBox");
        dialogImpl.setWidget(focusPanel);
        focusPanel.setWidget(borderInnerPanel);
        focusPanel.setStylePrimaryName("ugat-ModalDialog-FocusPanel");

        // Set Styles
		borderInnerPanel.setStyleName("ugat-ModalDialog-BorderInnerPanel");
		footerPanel.setStyleName("ugat-ModalDialog-Footer");
		centerPanel.setStyleName("ugat-ModalDialog-Center");
		aceitarBtn.addStyleName("ugat-ModalDialog-AceitarBtn");
		cancelarBtn.addStyleName("ugat-ModalDialog-CancelarBtn");

		// Setup borders
		borderInnerPanel.add(centerPanel);
		borderInnerPanel.add(footerPanel);

		borderInnerPanel.setPixelSize(width, height);

		if ((buttonStyle & BUTTONS_CANCEL)>0)
			footerPanel.add(cancelarBtn);
		
		if ((buttonStyle & BUTTONS_OK)>0) {
			footerPanel.add(aceitarBtn);
			setDefaultButton(aceitarBtn);
		}
		
		
		//add event handlers
		cancelarBtn.addClickListener(new CDialog_closeButton_onClickAdapter());
		aceitarBtn.addClickListener(new CDialog_aceitarBtn_onClickAdapter());
		focusPanel.addKeyPressHandler(this);
	}
	
	public void addActionListener(ModalDialog_ActionListener listener) {
		actionListeners.add(listener);
	}
	
	public void setDefaultButton(Button btn) {
		if ( footerBtnList.contains(btn) || (btn==cancelarBtn) || (btn==aceitarBtn) ) {
			if (defaultBtn!=null)
				defaultBtn.removeStyleName("ugat-DefaultButton");
			defaultBtn=btn;
			defaultBtn.addStyleName("ugat-DefaultButton");
		}
	}
	
	public void addFooterButton(Button btn) {
		footerBtnList.add(btn);
		footerPanel.add(btn);
	}
	
	public void clearFooterButtons() {
		for (int index=0; index<footerBtnList.size(); index++)
			footerPanel.remove((Widget)footerBtnList.get(index));
		footerBtnList.clear();
	}
	
	public void close() {
		hide();
	}
	
	//CDialog - Event handlers
	
	protected Object evalModalResult() throws Exception {
		return null;
	}

	public Object getModalResult() {
		return modalResult;
	}
	
	public void hide() {
		RootPanel.get().remove(lightBox);
		dialogImpl.hide();
	}
	
	public boolean isVisible() {
		return dialogImpl.isVisible();
	}
	
	protected final void onAceitar() {
		for (ModalDialog_ActionListener listener:actionListeners)
			listener.aceitarAction(getModalResult());
	}
	
	protected final void onCancelar() {
		for (ModalDialog_ActionListener listener:actionListeners)
			listener.cancelarAction();
	}

	public void removeActionListener(ModalDialog_ActionListener listener) {
		actionListeners.remove(listener);
	}

	public void removeFooterButton(Button btn) {
		footerBtnList.remove(btn);
		footerPanel.remove(btn);
	}
	
	public void setHideOnAceitar(boolean hideOnAceitar) {
		this.hideOnAceitar = hideOnAceitar;
	}

	public void setWidget(Widget w) {
	    // If there is already a widget, remove it.
	    if (child != null) {
	    	centerPanel.remove(child);
	    }
	
	    // Add the widget to the center of the dock panel.
	    if (w != null) {
	    	centerPanel.add(w);
	    }
	
	    child = w;
	}
	
	protected void aceitar()
	{
		aceitarBtn.click();
	}
	
	protected void cancelar()
	{
		cancelarBtn.click();
	}

	public void setTitle(String title) {
		dialogImpl.setText(title);
	}
	
	public final void show() {
		RootPanel.get().add(lightBox);
		dialogImpl.center();
		dialogImpl.show();
		DeferredCommand.addCommand(new Command() {
			@Override
			public void execute() {
				focusPanel.setFocus(true);
			}
		});
	}
	
	public static void showAlertDialog(String title, String message) {
		HorizontalPanel hPanel=new HorizontalPanel();
		ModalDialog dlg=new ModalDialog(title, 450, 160, ModalDialog.BUTTONS_OK) {
		};
		
		dlg.centerPanel.add(hPanel);
		hPanel.setSpacing(10);
		hPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		hPanel.add(new Image(GWT.getHostPageBaseURL()+"img/dialog-warning.png"));
		hPanel.add(new Label(message));
		dlg.show();
	}

	public void onKeyPress(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode()==13) {
			if (defaultBtn!=null)
				defaultBtn.click();
		}
		
		if (event.getNativeEvent().getKeyCode()==27)
			cancelarBtn.click();
	}
	
	public static void showConfirmDialog(String title, String message, final ModalDialog_ActionHandler handler) {
		HorizontalPanel hPanel=new HorizontalPanel();
		ModalDialog dlg=new ModalDialog(title, 450, 160, ModalDialog.BUTTONS_OK_CANCEL) {
		};
		
		dlg.addActionListener(new ModalDialog_ActionListener() {
			@Override
			public void cancelarAction() {
				handler.onAction(ACTION_CANCEL, false);
			}
			
			@Override
			public void aceitarAction(Object result) {
				handler.onAction(ACTION_OK, true);
			}
		});
		
		dlg.centerPanel.add(hPanel);
		hPanel.setSpacing(10);
		hPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		hPanel.add(new Image(GWT.getHostPageBaseURL()+"img/help-browser.png"));
		hPanel.add(new Label(message));
		dlg.show();
	}
	
	public static void showPromptDialog(String title, String message, String defaultValue, String[] suggestedOptions, final String valueRequiredMsg, final ModalDialog_ActionHandler handler) {
		HorizontalPanel hPanel=new HorizontalPanel();
		VerticalPanel vPanel=new VerticalPanel();
		final Label errorLabel=new Label("");
		final SafeTextBox textBox;
		final ModalDialog dlg=new ModalDialog(title, 450, 180, ModalDialog.BUTTONS_OK_CANCEL) {
		};

		
		if (suggestedOptions==null) {
			textBox=new SafeTextBox();			
		} else {
			textBox=new AutoCompleteTextBox();
			((AutoCompleteTextBox)textBox).setCompletionItems(new SimpleAutoCompletionItems(suggestedOptions));
		}
		
		errorLabel.setStylePrimaryName("ugat-ModalDialog-ErrorLabel");
		if (valueRequiredMsg!=null)
			errorLabel.setText(valueRequiredMsg);
		errorLabel.setVisible(false);
		
		dlg.setHideOnAceitar(false);
		
		dlg.addActionListener(new ModalDialog_ActionListener() {
			@Override
			public void cancelarAction() {
				handler.onAction(ACTION_CANCEL, "");
			}
			
			@Override
			public void aceitarAction(Object result) {
				if ( (valueRequiredMsg!=null) && (textBox.getText().trim().length()==0) ) {
					errorLabel.setVisible(true);
				} else {
					handler.onAction(ACTION_OK, textBox.getText());
					dlg.close();
				}
			}
		});
		
		dlg.centerPanel.add(hPanel);
		hPanel.setSpacing(10);
		hPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		hPanel.add(new Image(GWT.getHostPageBaseURL()+"img/help-browser.png"));
		hPanel.add(vPanel);
		vPanel.setSpacing(5);
		vPanel.add(new Label(message));
		textBox.setText(defaultValue);
		textBox.setWidth("280px");
		vPanel.add(textBox);
		vPanel.add(errorLabel);
		dlg.show();
	}	

	public static void showPromptDialog(String title, String message, String defaultValue, String valueRequiredMsg, ModalDialog_ActionHandler handler) {
		showPromptDialog(title, message, defaultValue, null, valueRequiredMsg, handler);
	}

	public static void showPromptDialog(String title, String message, String defaultValue, ModalDialog_ActionHandler handler) {
		showPromptDialog(title, message, defaultValue, null, null, handler);
	}
}
