package org.ucp.gwt.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class Balloon implements ClickListener {
	static final int ARROW_V_PIXELS = 6;
	static final int ARROW_H_PIXELS = 10;
	static final int PREFERENCE_MARGIN = 20;
	static final int BALLOON_BORDER = 6;
	
	private int refLeft, refTop;
	private AbsolutePanel panel;
	private int top, left, width, height, arrowLeft, arrowTop;
	private boolean toTop;
	private Image a, tl, tr, bl, br;
	private Label t, l, r, b;
	private Timer timer;
	private boolean drawn = false;
	private boolean hidden = true;
	private Widget widget;
	
	private List commandQueue = new ArrayList();
	
	private boolean shaking = false;
	private int shakeTime = 0;
	private Timer shakeTimer;
	
	Balloon(int refLeft, int refTop, int left, int top, int width, int height, int arrowLeft, int arrowTop, boolean toTop) {
		this.refLeft = refLeft;
		this.refTop = refTop;
		this.top = top;
		this.left = left;
		this.width = width;
		this.height = height;
		this.arrowLeft = arrowLeft;
		this.arrowTop = arrowTop;
		this.toTop = toTop;
	}
	
	public static Balloon make(Widget target, int width, int height, boolean prefersTop) {
		return new BalloonMaker(target, width, height, prefersTop).make();
	}
	
	public static Balloon make(int focalPointLeft, int focalPointTop, int width, int height, boolean prefersTop) {
		return new BalloonMaker(focalPointLeft, focalPointTop, width, height, prefersTop).make();
	}
	
	public void move(int newRefLeft, int newRefTop) {
		if ( refLeft == -1 ) throw new IllegalStateException();
		if ( newRefLeft == refLeft && newRefTop == refTop ) return;
		
		int widthDelta = newRefLeft - refLeft;
		int heightDelta = newRefTop - refTop;
		
		refLeft = newRefLeft;
		refTop = newRefTop;
		
		left += widthDelta;
		top += heightDelta;
		
		arrowLeft += widthDelta;
		arrowTop += heightDelta;
		
		AbsolutePanel root = RootPanel.get();
		
		root.add(panel, left, top);
		root.add(a, arrowLeft, arrowTop);
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void shake() {
		shake(null);
	}
	
	private void runCommandQueue() {
		for ( int i = 0 ; i < commandQueue.size() ; i++ ) {
			Command x = (Command)commandQueue.get(i);
			DeferredCommand.addCommand(x);
		}
		commandQueue.clear();
	}
	
	public void shake(Command command) {
		if ( !drawn ) throw new IllegalStateException();
		if ( command != null ) commandQueue.add(command);
		if ( hidden ) unhide();
		if ( shaking ) stopShake();
		
		if ( shakeTimer == null ) shakeTimer = new Timer() {
			public void run() {
				doShake();
			}
		};
		
		shakeTime = 0;
		
		shakeTimer.scheduleRepeating(20);
		shaking = true;
	}
	
	private void doShake() {
		if ( shakeTime++ == 24 ) stopShake();
		int diff;
		switch ( shakeTime % 12 ) {
		case 5:
		case 1: diff = 5; break;
		case 4:
		case 2: diff = 10; break;
		case 3: diff = 15; break;
		case 11:
		case 7: diff = -5; break;
		case 10:
		case 8: diff = -10; break;
		case 9: diff = -15; break;
		default: diff = 0;
		}
		
		RootPanel.get().setWidgetPosition(panel, left + diff, top);
	}
	
	private void stopShake() {
		if ( shaking ) {
			shakeTimer.cancel();
			RootPanel.get().setWidgetPosition(panel, left, top);
			shaking = false;
		}
		runCommandQueue();
	}
	
	public void unhide() {
		if ( !drawn ) throw new IllegalStateException();
		if ( timer != null ) timer.cancel();
		if ( hidden ) {
			stopShake();
			this.hidden = false;
			AbsolutePanel root = RootPanel.get();
			root.add(panel, left, top);
			root.add(a, arrowLeft, arrowTop);
		}
	}
	
	public void draw(Widget content) {
		if ( drawn ) throw new IllegalStateException();
		this.drawn = true;
		this.hidden = false;
		this.widget = content;
		setBalloonBackgroundStyle(widget);
		String dir = toTop ? "Down" : "Up";
		a = new Image(GWT.getModuleBaseURL()+"img/balloon" + dir + ".gif");
		a.setStyleName("ucpgwt-Balloon-Arrow");
		a.setPixelSize(ARROW_H_PIXELS, ARROW_V_PIXELS);
		tl = new Image(GWT.getModuleBaseURL()+"img/balloonTL.gif");
		tl.setPixelSize(BALLOON_BORDER, BALLOON_BORDER);
		tr = new Image(GWT.getModuleBaseURL()+"img/balloonTR.gif");
		tr.setPixelSize(BALLOON_BORDER, BALLOON_BORDER);
		bl = new Image(GWT.getModuleBaseURL()+"img/balloonBL.gif");
		bl.setPixelSize(BALLOON_BORDER, BALLOON_BORDER);
		br = new Image(GWT.getModuleBaseURL()+"img/balloonBR.gif");
		br.setPixelSize(BALLOON_BORDER, BALLOON_BORDER);
		
		l = new Label();
		DOM.setStyleAttribute(l.getElement(), "backgroundImage", "url("+GWT.getModuleBaseURL()+"img/balloonL.gif)");
		l.setPixelSize(BALLOON_BORDER, height);
		l.addClickListener(this);
		r = new Label();
		DOM.setStyleAttribute(r.getElement(), "backgroundImage", "url("+GWT.getModuleBaseURL()+"img/balloonR.gif)");
		r.setPixelSize(BALLOON_BORDER, height);
		r.addClickListener(this);
		t = new Label();
		DOM.setStyleAttribute(t.getElement(), "backgroundImage", "url("+GWT.getModuleBaseURL()+"img/balloonT.gif)");
		t.setPixelSize(width, BALLOON_BORDER);
		t.addClickListener(this);
		b = new Label();
		DOM.setStyleAttribute(b.getElement(), "backgroundImage", "url("+GWT.getModuleBaseURL()+"img/balloonB.gif)");
		b.setPixelSize(width, BALLOON_BORDER);
		b.addClickListener(this);
		
		if ( content instanceof SourcesClickEvents ) {
			((SourcesClickEvents)content).addClickListener(this);
		}
		
		AbsolutePanel root = RootPanel.get();
		panel = new AbsolutePanel();
		panel.addStyleName("ucpgwt-Balloon");
		
		root.add(a, arrowLeft, arrowTop);
		panel.add(tl, 0, 0);
		panel.add(t, BALLOON_BORDER, 0);
		panel.add(tr,  width + BALLOON_BORDER, 0);
		panel.add(l, 0, BALLOON_BORDER);
		panel.add(r, width + BALLOON_BORDER, BALLOON_BORDER);
		panel.add(bl, 0, height + BALLOON_BORDER);
		panel.add(b, BALLOON_BORDER, height + BALLOON_BORDER);
		panel.add(br, width + BALLOON_BORDER, height + BALLOON_BORDER);
		content.setPixelSize(width, height);
		panel.add(content, BALLOON_BORDER, BALLOON_BORDER);
		panel.setPixelSize(width + 2 * BALLOON_BORDER, height + 2 * BALLOON_BORDER);
		root.add(panel, left, top);
	}
	
	public void draw(String text) {
		draw(text, "balloon");
	}
	
	public void draw(String text, String style) {
		Label label = new Label(text);
		label.addStyleName(style);
		draw(label);
	}
	
	public void setText(String text) {
		if ( !drawn ) draw(text);
		else if ( widget instanceof Label ) {
			((Label)widget).setText(text);
			unhide();
		}
		else throw new IllegalStateException();
	}
	
	public void setText(String text, String style) {
		setText(text);
		setBalloonBackgroundStyle(widget);
		widget.addStyleName(style);
	}
	
	public String getText() {
		if ( widget instanceof Label ) return ((Label)widget).getText();
		else throw new IllegalStateException();
	}
	
	public void hide() {
		if ( !drawn ) throw new IllegalStateException();
		if ( timer != null ) timer.cancel();
		if ( hidden ) return;
		this.hidden = true;
		stopShake();
		AbsolutePanel root = RootPanel.get();
		root.remove(a);
		root.remove(panel);
	}
	
	public static void hide(Balloon balloon) {
		if ( balloon != null ) balloon.hide();
	}
	
	public void timedHide(int millis) {
		if ( timer != null ) timer.cancel();
		else timer = new Timer() {
			public void run() {
				hide();
			}
		};
		
		timer.schedule(millis);
	}
	
	public void onClick(Widget sender) {
		hide();
	}
	
	private void setBalloonBackgroundStyle(UIObject e) {
		Element h = e.getElement();
		
		DOM.setStyleAttribute(h, "padding", "0px 0px 0px 0px");
		DOM.setStyleAttribute(h, "margin", "0px 0px 0px 0px");
		DOM.setStyleAttribute(h, "borderStyle", "none");
		DOM.setStyleAttribute(h, "backgroundImage", "url("+GWT.getModuleBaseURL()+"img/balloonC.gif)");
	}
}
