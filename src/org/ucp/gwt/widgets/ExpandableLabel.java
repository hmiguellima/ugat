package org.ucp.gwt.widgets;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class ExpandableLabel extends Composite {
	class ExpandableLabel_ContainerPanel extends FlowPanel {
		
		public ExpandableLabel_ContainerPanel() {
			sinkEvents(Event.MOUSEEVENTS);
		}
		
		public void onBrowserEvent(Event event) {
			super.onBrowserEvent(event);
			if (captionWidget!=null)
				switch (DOM.eventGetType(event)) {
					case Event.ONMOUSEOVER:
						captionWidget.setVisible(true);
						break;
					case Event.ONMOUSEOUT:
						captionWidget.setVisible(false);
						break;
				}
		}
	}
	
	class ExpandableLabel_onClick implements ClickListener {
		public void onClick(Widget sender) {
			isCompact=!isCompact;
			if (isCompact) {
				toogleImg.setUrl(COMPACT_IMG_URL);
				captionLabel.setTitle(EXPAND_TITLE);
				contentPanel.setVisible(false);
			} else {
				toogleImg.setUrl(EXPAND_IMG_URL);
				captionLabel.setTitle(COMPACT_TITLE);
				contentPanel.setVisible(true);
			}
		}
	}
	
	private ExpandableLabel_ContainerPanel panel;
	private Image toogleImg=new Image();
	private Label captionLabel=new Label();
	private FlowPanel captionPanel=new FlowPanel();
	private static final String EXPAND_IMG_URL=GWT.getModuleBaseURL()+"img/expand-arrow.png";
	private static final String COMPACT_IMG_URL=GWT.getModuleBaseURL()+"img/compact-arrow.png";
	private HTML contentPanel=new HTML();
	private boolean isCompact=true;
	private static final String EXPAND_TITLE="Pressione para mostrar detalhes";
	private static final String COMPACT_TITLE="Pressione para ocultar detalhes";
	private Widget captionWidget=null;
	
	public ExpandableLabel(String caption, Widget captionWidget, String content) {
		panel=new ExpandableLabel_ContainerPanel();
		initWidget(panel);
		this.captionWidget=captionWidget;
		setStyleName("ucpgwt-ExpandableLabel");
		captionPanel.setStyleName("ucpgwt-ExpandableLabel-CaptionPanel");
		captionLabel.setStyleName("ucpgwt-ExpandableLabel-CaptionLabel");
		captionLabel.setText(caption);
		captionLabel.setTitle(EXPAND_TITLE);
		contentPanel.setStyleName("ucpgwt-ExpandableLabel-ContentPanel");
		contentPanel.setHTML(content);
		toogleImg.setStyleName("ucpgwt-ExpandableLabel-ToogleImg");
		toogleImg.setUrl(COMPACT_IMG_URL);
		captionPanel.add(toogleImg);
		captionPanel.add(captionLabel);
		panel.add(captionPanel);
		if (captionWidget!=null) {
			captionWidget.setVisible(false);
			panel.add(captionWidget);
		}
		panel.add(contentPanel);
		contentPanel.setVisible(false);
		captionLabel.addClickListener(new ExpandableLabel_onClick());
	}

	public ExpandableLabel(String caption, String content) {
		this(caption, null, content);
	}
}
