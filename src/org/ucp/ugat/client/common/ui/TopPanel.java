package org.ucp.ugat.client.common.ui;

import org.ucp.ugat.client.entry.*;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class TopPanel extends Grid {
	private static final String TOKEN_EXIT="exit";
	private static final String PARAM_REFERRER="ref";
	private static final String PARAM_REFERRER_NAME="refname";
	private static final String PARAM_REFERRER_BLINK="refblink";
	private static final String STYLE_BLINK="ugat-Blink";
	private static final int BLINK_COUNT=20;
	private static final int BLINK_SPEED=500;

	private static int blinkCounter=0;
	private Timer blinkTimer;
	
	private Anchor buildReturnAnchor() {
		final Anchor anchor;
		
		if (!Window.Location.getParameterMap().containsKey(PARAM_REFERRER_NAME))
			anchor=new Anchor("Voltar", Window.Location.getParameter(PARAM_REFERRER));
		else
			anchor=new Anchor(" <Voltar a "+Window.Location.getParameter(PARAM_REFERRER_NAME)+">", Window.Location.getParameter(PARAM_REFERRER));
		
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AppEntry.getCurrentModule().setConfirmModuleExit(false);
			}
		});

		if (Window.Location.getParameterMap().containsKey(PARAM_REFERRER_BLINK)) {
			blinkTimer=new Timer() {
				@Override
				public void run() {
					if (anchor.getStyleName().indexOf(STYLE_BLINK)<0) 
						anchor.addStyleName(STYLE_BLINK);
					else 
						anchor.removeStyleName(STYLE_BLINK);
					blinkCounter++;
					if (blinkCounter<BLINK_COUNT)
						blinkTimer.schedule(BLINK_SPEED);
				}
			};
			blinkTimer.schedule(BLINK_SPEED);
		}
			
		
		return anchor;
	}
	
	public TopPanel(String moduleName, String loggedUser, String iconURL, String ajudaURL) {
		super(1, 2);
		
		Grid vGrid;
		String welcomeMsg;
		Widget sairBtn;
		HTML sobreBtn;
		FlowPanel linksPanel;
		HTML welcomeLbl;
		HTML titleLbl;
		
		
		setStyleName("ugat-TopPanel");

		titleLbl=new HTML("<img id='ugat-TopPanel-Icon' class='ugat-TopPanel-TitleIcon' style='display:none;' src='"+iconURL+"'/>&nbsp;"+moduleName);
		titleLbl.setStyleName("ugat-TopPanel-Title");

		welcomeMsg="Bem-vindo(a), "+loggedUser;
		welcomeLbl=new HTML(welcomeMsg.replaceAll(" ", "&nbsp;"));
		welcomeLbl.setStyleName("ugat-TopPanel-Welcome");
		
		if (!Window.Location.getParameterMap().containsKey(PARAM_REFERRER))
			sairBtn=new Hyperlink("Sair", TOKEN_EXIT);
		else
			sairBtn=buildReturnAnchor();
			
		sairBtn.setStyleName("ugat-TopPanel-Links");
		sobreBtn=new HTML("<a target='help' href='"+ajudaURL+"'>Ajuda</a>");
		sobreBtn.setStyleName("ugat-TopPanel-Links");

		linksPanel=new FlowPanel();
		linksPanel.add(sairBtn);
		linksPanel.add(sobreBtn);
		
		vGrid=new Grid(2, 1);
		vGrid.setHeight("100%");
		vGrid.setWidget(0, 0, titleLbl);
		vGrid.setWidget(1, 0, new HTML("&nbsp;"));
		vGrid.getCellFormatter().setHeight(1, 0, "100%");
		setWidget(0, 0, vGrid);
		

		vGrid=new Grid(3, 1);
		vGrid.setHeight("100%");
		vGrid.setWidget(0, 0, welcomeLbl);
		vGrid.getCellFormatter().setHeight(1, 0, "100%");
		vGrid.setWidget(2, 0, linksPanel);
		vGrid.getCellFormatter().setHorizontalAlignment(2, 0, HorizontalPanel.ALIGN_RIGHT);
		setWidget(0, 1, vGrid);

		getCellFormatter().setWidth(0, 0, "100%");
	}
	
}
