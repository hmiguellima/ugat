package org.ucp.gwt.widgets;

/*
 * CMenu is the main class of a menu bar system built for
 * simplicity, usability and flexibility.
 * 
 *  It's main features are:
 *  
 *  - One line options grid.
 *  - Automatic resize and hiding of options with a pull-down menu appearing
 *    when all options don't fit the container.
 *  - Menu context switching providing multiple layers of options.
 *  - String key access to all of the menu system components (menus, 
 *    contexts, items). 
 *  
 */
import java.util.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class CMenu extends FlowPanel {
	private CListBox contextBox=new CListBox();
	private HashMap contextMap=new HashMap();
	private HTML disablePanel=null;
	private Grid grid=new Grid(1, 2);
	private int selectedContext=-1;
	private boolean isLoaded=false;
	private boolean isBuilding=false;
	private Image waitIcon=new Image(GWT.getModuleBaseURL()+"img/mozilla_blu.gif");
	
	public CMenu() {
		setStyleName("ucpgwt-CMenu");
		add(waitIcon);
		Window.addWindowResizeListener(new WindowResizeListener() {
			public void onWindowResized(int width, int height) {
				buildContextBar();
			}
		});
		isBuilding=false;
	}
	
	public void addContext(String key, CMenuContext context) throws Exception {
		if (contextMap.size()==0)
			selectedContext=0;
		else 
			if (contextMap.get(key)!=null) {
				throw new Exception("CMenu::addContext:Contexto já existe");
			}
		contextBox.addItem(context.getName(), context);
		contextMap.put(key, context);
		context.setMenu(this);
	}
	
	public void buildContextBar() {
		if ( !isLoaded || isBuilding) return;
		
		isBuilding=true;
		
		DeferredCommand.addCommand(new Command() {
			@Override
			public void execute() {
				HorizontalPanel hPanel=new HorizontalPanel();
				CMenuItem item;
				int clientWidth=Window.getClientWidth();
				int cListWidth;
				int contextCount=contextMap.size();
				
				isBuilding=false;
				
				if (contextCount>1)
					cListWidth=grid.getWidget(0, 0).getOffsetWidth();
				else
					cListWidth=0;

				grid.setWidget(0, 1, hPanel);
				for (int conta=0;conta<contextCount;conta++) {
					if (conta==selectedContext) {
						CMenuContext context=getContext(conta);
						
						for (int conta2=0;conta2<context.itemCount();conta2++) {
							item=context.getItem(conta2);
							hPanel.add(item);
							// Se não couberem mais vamos por o resto num MenuBar
							if (hPanel.getOffsetWidth()+100>clientWidth-cListWidth) {
								CMenuPopup outrosMenu=new CMenuPopup(true);
								hPanel.remove(item);
								int popupItemCount=0;
								for (int conta3=conta2;conta3<context.itemCount();conta3++) {
									item=context.getItem(conta3);
									if (!item.isVisible()) continue;
									outrosMenu.addItem(item.getPopupMenuItem());
									popupItemCount++;
								}
								if (popupItemCount>0) {
									CMenuPopup popupMenu=new CMenuPopup();
									popupMenu.addItem("Outros...", outrosMenu);
									hPanel.add(popupMenu);
								}
								break;
							}
						}
						break;
					}
				}
			}
		});
	}
	
	public void disable() {
		if (disablePanel==null) {
			disablePanel=new HTML("<div class='ucpgwt-CMenu-Disabled'></div>");
			add(disablePanel);
		}
	}
	
	public void enable() {
		if (disablePanel!=null) {
			remove(disablePanel);
		}
	}
	
	public CMenuContext getContext(int index) {
		return (CMenuContext)contextBox.getUserObject(index);
	}
	
	public CMenuContext getContext(String key) {
		return (CMenuContext)contextMap.get(key);
	}

	public void onLoad() {
		int contextCount=contextMap.size();
		
		contextBox.setStyleName("ucpgwt-CMenu-ContextList");
		contextBox.setVisibleItemCount(1);
		contextBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				Command cmd;

				setContext(contextBox.getSelectedIndex());
				cmd=getContext(selectedContext).getCmd();
				if (cmd!=null)
					cmd.execute();
			}
		});
		
		if ( (contextCount!=0) && (getContext(0).getCmd()!=null) ) {
			getContext(0).getCmd().execute();
		}
		
		if (contextCount>1)
			grid.setWidget(0, 0, contextBox);
		grid.getCellFormatter().setWidth(0, 1, "100%");

		DeferredCommand.addCommand(new Command() {
			@Override
			public void execute() {
		  		remove(waitIcon);
				add(grid);
		        isLoaded=true;
		        buildContextBar();
			}
		});
	}

	public void setContext(CMenuContext context) {
		if (!contextBox.getUserObject(selectedContext).equals(context) ) {
			contextBox.setSelectedObject(context);
			selectedContext=contextBox.getSelectedIndex();
			buildContextBar();
		}
	}
	
	public void setContext(int index) {
		if (selectedContext!=index) {
			selectedContext=index;
			contextBox.setSelectedIndex(selectedContext);
			buildContextBar();
		}
	}
}
