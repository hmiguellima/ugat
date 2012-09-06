package org.ucp.gwt.widgets;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class TooltipMouseListener extends MouseListenerAdapter implements EventListener {
	class TooltipMouseListener_Timer extends Timer {
		private boolean sleeping=false;
		private int x=-1, y=-1;
		
		public boolean isSleeping() {
			return sleeping;
		}

		public void run() {
			sleeping=false;
			if ( (x==mouseX) && (y==mouseY) )
				showPopup();
		}

		public void schedule(int x, int y) {
			this.x=x;
			this.y=y;
			if (sleeping) {
				cancel();
				schedule(popupDelayMS);
			} else {
				sleeping=true;
				schedule(popupDelayMS);
			}
		}
	}
	
	private static int popupDelayMS=800;
	
	public static void setPopupDelayMS(int popupDelayMS) {
		TooltipMouseListener.popupDelayMS = popupDelayMS;
	}
	
	private UIObject boundObj=null;
	private FocusPanel focusPanel=new FocusPanel();
	private FlowPanel glassPanel=new FlowPanel();
	private int mouseX, mouseY;
	private FlowPanel panel=new FlowPanel();
	private PopupPanel popup=null;
	private TooltipMouseListener_Timer popupTimer=new TooltipMouseListener_Timer();
	private DynamicPosition position=null;
	private HTML tipHTML;
	private boolean useObjWidth=true;
	
	private Label titleLabel;

	public TooltipMouseListener(DynamicPosition position, String title, String tip) {
		this(title, tip);
		this.position=position;
	}
	
	private TooltipMouseListener(String title, String tip) {
		String safeHTMLStr;
		
		tipHTML=new HTML();
		tipHTML.setStyleName("ucpgwt-TooltipMouseListener-TipLabel");
		safeHTMLStr=new String(tip);
		safeHTMLStr=safeHTMLStr.replaceAll("<", "&lt;");
		safeHTMLStr=safeHTMLStr.replaceAll(">", "&gt;");
		safeHTMLStr=safeHTMLStr.replaceAll("\n", "<br/>");
		safeHTMLStr=safeHTMLStr.replaceAll("\r", "");
		safeHTMLStr=safeHTMLStr.replaceAll(" ", "&nbsp;");
		tipHTML.setHTML(safeHTMLStr);
		titleLabel=new Label(title);
		titleLabel.setStyleName("ucpgwt-TooltipMouseListener-TitleLabel");
		focusPanel.setWidget(panel);
		panel.add(titleLabel);
		panel.add(tipHTML);
		glassPanel.setStyleName("ucpgwt-TooltipMouseListener-GlassPanel");
		panel.add(glassPanel);
		focusPanel.setStyleName("ucpgwt-TooltipMouseListener-PopupPanel");
		focusPanel.addMouseListener(new MouseListenerAdapter() {
			public void onMouseLeave(Widget sender) {
				if ( (popup!=null) && popup.isVisible() )
					popup.hide();
			}

			public void onMouseUp(Widget sender, int x, int y) {
				if ( (popup!=null) && popup.isVisible() ) 
					popup.hide();
			}
		});
		focusPanel.addFocusListener(new FocusListener() {
			public void onFocus(Widget sender) {
			}

			public void onLostFocus(Widget sender) {
				if ( (popup!=null) && popup.isVisible() )
					popup.hide();
			}
		});
	}
	
	public TooltipMouseListener(UIObject boundObj, String title, String tip) {
		this(title, tip);
		this.boundObj=boundObj;
	}

	public TooltipMouseListener(UIObject boundObj, boolean useObjWidth, String title, String tip) {
		this(boundObj, title, tip);
		this.useObjWidth=useObjWidth;
	}
	
	public void onBrowserEvent(Event event) {
		int type;
		
		type=DOM.eventGetType(event);
		switch (type) {
			case Event.ONMOUSEMOVE:
				mouseX=DOM.eventGetClientX(event);
				mouseY=DOM.eventGetClientY(event);
				popupTimer.schedule(mouseX, mouseY);
				break;
			case Event.ONMOUSEOUT:
				if (popupTimer.isSleeping())
					popupTimer.cancel();
				break;
		}
	}

	public void onMouseLeave(Widget sender) {
		if (popupTimer.isSleeping())
			popupTimer.cancel();
	}

	public void onMouseMove(Widget sender, int x, int y) {
		mouseX=x;
		mouseY=y;
		popupTimer.schedule(mouseX, mouseY);
	}

	public void showPopup() {
		popup=new PopupPanel(false);
		popup.setWidget(focusPanel);
		if (boundObj!=null) {
			if (useObjWidth)
				popup.setWidth(String.valueOf(boundObj.getOffsetWidth())+"px");
			popup.setPopupPosition(boundObj.getAbsoluteLeft(), boundObj.getAbsoluteTop());
		} else {
			popup.setPopupPosition(position.getLeft(), position.getTop());
		}
		popup.show();
		focusPanel.setFocus(true);
	}
}
