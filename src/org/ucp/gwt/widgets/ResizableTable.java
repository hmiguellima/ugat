package org.ucp.gwt.widgets;

import java.util.*;

import org.ucp.gwt.util.*;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class ResizableTable extends FlexTable {
	private boolean isResizing;
	private Element currCell=null;
	private int currCellIndex=-1;
	private int oldX;
	private ArrayList columnResizeListeners=new ArrayList();
	private static final boolean DEBUG=false;
	
	public ResizableTable() {
		setStyleName("ucpgwt-ResizableTable");
		sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEMOVE | Event.ONMOUSEUP);
	}
	
	public void addColumnResizeListener(ColumnResizeListener listener) {
		columnResizeListeners.add(listener);
	}

	public void removeColumnResizeListener(ColumnResizeListener listener) {
		columnResizeListeners.remove(listener);
	}
	
	public void onBrowserEvent(Event event) {
		Element cell=null;
		int scrollLeft=0, eventX=0, cellLeft=0, cellWidth=0, deltaX=0;
		int newWidth, borderDistance;
		//Element testElement=null;
		
		super.onBrowserEvent(event);

		if (!isResizing) {
			cell=getEventTargetCell(event);
			if (cell==null){
				if (currCell!=null) {
					DOM.setStyleAttribute(currCell, "cursor", "default");
					currCell=null;
				}
				return;
			}
		}

		switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEMOVE:
				eventX=DOM.eventGetClientX(event);
				//scrollLeft=DOM.getElementPropertyInt(getElement(), "scrollLeft");
				//testElement=getElement();
				//while ((testElement=DOM.getParent(testElement))!=null)
				//	scrollLeft+=DOM.getElementPropertyInt(testElement, "scrollLeft");
				scrollLeft=WindowUtils.getScrollLeft();
				if (!isResizing) {
					cellLeft=DOM.getAbsoluteLeft(cell);
					cellWidth=DOM.getElementPropertyInt(cell, "width");
					borderDistance=cellWidth-(eventX+scrollLeft-cellLeft);
					if ( (borderDistance>=-10) && (borderDistance<=10) ) {
						DOM.setStyleAttribute(cell, "cursor", "w-resize");
						currCell=cell;
						currCellIndex=DOM.getChildIndex(DOM.getParent(currCell), currCell);
					} else {
						DOM.setStyleAttribute(cell, "cursor", "default");
						currCell=null;
					}
				} else {
					cellWidth=DOM.getElementPropertyInt(currCell, "width");
					deltaX=eventX-oldX;
					oldX=eventX;
					newWidth=cellWidth+deltaX;
					// Prevent the cell from disapearing
					if (newWidth<5)
						newWidth=5;
					DOM.setElementProperty(currCell, "width", String.valueOf(newWidth)+"px");
					if (DEBUG)
						DOM.setElementProperty(currCell, "title", String.valueOf(newWidth)+"px");
					fireColumnResizeEvent(currCellIndex, newWidth);
				}
				break;
			case Event.ONMOUSEDOWN:
				if (currCell!=null) {
					DOM.setCapture(getElement());
					isResizing=true;
					oldX=DOM.eventGetClientX(event);
					unsinkEvents(Event.ONCLICK);
				}
				break;
			case Event.ONMOUSEUP:
				if (isResizing) {
					DOM.releaseCapture(getElement());
					isResizing=false;
					DeferredCommand.addCommand(new Command() {
						public void execute() {
							sinkEvents(Event.ONCLICK);
						}
					});
				}
				break;
		}
	}
	
	private void fireColumnResizeEvent(int column, int width) {
		for (int index=0;index<columnResizeListeners.size();index++) 
			((ColumnResizeListener)(columnResizeListeners.get(index))).onColumnResize(column, width);
	}
}
