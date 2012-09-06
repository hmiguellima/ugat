package org.ucp.gwt.widgets;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

class BalloonMaker {
	private int widgetLeft, widgetTop, widgetWidth, widgetHeight;
	private int screenWidth, screenHeight;
	private int balloonWidth, balloonHeight;
	
	private int originalLeft, originalTop;
	
	private boolean toTop;
	
	private int balloonLeft, balloonTop, arrowLeft, arrowTop;
	
	BalloonMaker(Widget target, int width, int height, boolean prefersTop) {
		originalLeft = -1;
		originalTop = -1;
		widgetLeft = target.getAbsoluteLeft();
		widgetTop = target.getAbsoluteTop();
		widgetWidth = target.getOffsetWidth();
		widgetHeight = target.getOffsetHeight();
		
		init(width, height, prefersTop);
	}
	
	BalloonMaker(int focalLeft, int focalTop, int width, int height, boolean prefersTop) {
		originalLeft = focalLeft;
		originalTop = focalTop;
		
		widgetLeft = focalLeft;
		widgetWidth = 0;
		widgetTop = focalTop - 2;
		widgetHeight = 4;
		
		init(width, height, prefersTop);
	}
	
	private void init(int width, int height, boolean prefersTop) {
		screenWidth = RootPanel.get().getOffsetWidth();
		screenHeight = RootPanel.get().getOffsetHeight();
		
		balloonWidth = width;
		balloonHeight = height;
		
		toTop = decideTopOrBottom(prefersTop);
		decideHorizontal();
		decideVertical(toTop);
	}
	
	Balloon make() {
		return new Balloon(originalLeft, originalTop, balloonLeft, balloonTop, balloonWidth, balloonHeight, arrowLeft, arrowTop, toTop);
	}
	
	private void decideHorizontal() {
		int widgetMiddle = widgetLeft + (widgetWidth /2);
		int screenMiddle = screenWidth / 2;
		boolean goLeft = widgetMiddle > screenMiddle;
		
		if ( goLeft ) {
			balloonLeft = widgetMiddle - balloonWidth - Balloon.BALLOON_BORDER + 50;
			if ( balloonLeft < Balloon.PREFERENCE_MARGIN ) balloonLeft = Balloon.PREFERENCE_MARGIN;
		} else {
			balloonLeft = widgetMiddle - 50 - Balloon.BALLOON_BORDER;
			if ( screenWidth - (balloonLeft + Balloon.BALLOON_BORDER + Balloon.BALLOON_BORDER + balloonWidth) < Balloon.PREFERENCE_MARGIN )
				balloonLeft = screenWidth - Balloon.PREFERENCE_MARGIN - (Balloon.BALLOON_BORDER *2) - balloonWidth;
		}
		
		arrowLeft = widgetMiddle - (Balloon.ARROW_H_PIXELS / 2);
	}
	
	private void decideVertical(boolean toTop) {
		if ( toTop ) {
			arrowTop = widgetTop - Balloon.ARROW_V_PIXELS;
			balloonTop = arrowTop - (Balloon.BALLOON_BORDER + Balloon.BALLOON_BORDER + balloonHeight);
		} else {
			arrowTop = widgetTop + widgetHeight;
			balloonTop = arrowTop + Balloon.ARROW_V_PIXELS;
		}
	}
	
	/** Checks screen, widget, and requested balloon parameters to figure out where the balloon looks best. */
	private boolean decideTopOrBottom(boolean prefersTop) {
		if ( prefersTop ) {
			if ( fitsTop(Balloon.PREFERENCE_MARGIN) ) return true;
			else if ( fitsBottom(Balloon.PREFERENCE_MARGIN) ) return false;
			else return fitsTop(0);
		} else {
			if ( fitsBottom(Balloon.PREFERENCE_MARGIN) ) return false;
			else if ( fitsTop(Balloon.PREFERENCE_MARGIN) ) return true;
			else if ( fitsBottom(0) ) return false;
			else return fitsTop(0);
		}
	}
	
	/** Checks if the box would fit at the top with at least 'margin' pixels left over */
	private boolean fitsTop(int margin) {
		int needed = (Balloon.BALLOON_BORDER * 2) + balloonHeight + Balloon.ARROW_V_PIXELS + margin;
		return widgetTop > needed;
	}
	
	/** Checks if the box would fit at the bottom with at least 'margin' pixels left over */
	private boolean fitsBottom(int margin) {
		int needed = (Balloon.BALLOON_BORDER * 2) + balloonHeight + Balloon.ARROW_V_PIXELS + margin;
		return screenHeight - (widgetTop + widgetHeight) > needed;
	}
}
