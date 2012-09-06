package org.ucp.gwt.widgets;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class ImageButton extends Composite {
	class ImageButton_Panel extends FocusPanel {
		public void onBrowserEvent(Event event) {
			if (isEnabled)
				super.onBrowserEvent(event);
		}
	}
	
	private Image img;
	private ImageButton_Panel panel=new ImageButton_Panel();
	private ClickListenerCollection clickListeners=new ClickListenerCollection();
	private boolean isEnabled;

	public ImageButton(String url) {
		initWidget(panel);
		img=new Image(url);
		img.setStyleName("ucpgwt-ImageButton-Img");
		panel.add(img);
		setEnabled(true);
		panel.addMouseListener(new MouseListenerAdapter() {
			public void onMouseDown(Widget sender, int x, int y) {
				setStyleName("ucpgwt-ImageButton-Clicked");
			}

			public void onMouseUp(Widget sender, int x, int y) {
				setStyleName("ucpgwt-ImageButton");
			}
		});
		panel.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				clickListeners.fireClick(ImageButton.this);
			}
		});
	}
	
	public void setURL(String url) {
		panel.remove(img);
		img=new Image(url);
		img.setStyleName("ucpgwt-ImageButton-Img");
		panel.add(img);
	}
	
	public void setImage(Image img) {
		panel.remove(this.img);
		this.img=img;
		img.setStyleName("ucpgwt-ImageButton-Img");
		panel.add(img);
	}
	
	public void addClickListener(ClickListener listener) {
		clickListeners.add(listener);
	}
	
	public void removerClickListener(ClickListener listener) {
		clickListeners.remove(listener);
	}
	
	public void setEnabled(boolean isEnabled) {
		this.isEnabled=isEnabled;
		if (isEnabled)
			setStyleName("ucpgwt-ImageButton");
		else
			setStyleName("ucpgwt-ImageButton-Disabled");
	}

	@Override
	public void setHeight(String height) {
		super.setHeight(height);
		panel.setHeight(height);
	}
	
	public void click() {
		clickListeners.fireClick(ImageButton.this);
	}
}
