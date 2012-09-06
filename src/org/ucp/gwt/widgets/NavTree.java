package org.ucp.gwt.widgets;

/*
 * NavTree is an abstract class that extends the base GWT Tree 
 * with the following features:
 * - Select tree items by associated user object reference.
 * - Scrollable
 * - Automaticly scroll treeitem into view when selected (programatically).
 * - Abstract method prototypes for building the tree and handling tree events.
 *  
 */

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public abstract class NavTree extends FlowPanel {
	private static native void scrollIntoView(Element item) /*-{
		item.scrollIntoView();
	}-*/;
	public TreeItem root;
	
	public Tree tree;

	public NavTree(String rootName) {
		this.setStyleName("ucpgwt-NavTree");
		root=new CustomTreeItem(rootName);
		tree=new Tree() {
			  public void onBrowserEvent(Event event) {
	  		      int eventType = DOM.eventGetType(event);
			      if (eventType==Event.ONCLICK)
			        return;
			      else
			    	super.onBrowserEvent(event);
			  }
		};
		tree.addItem(root);
		add(tree);
		configureTreeListener();
	}
	
	public abstract void buildTree();

	public void clearSelection() {
		tree.setSelectedItem(null);
	}
	
	public abstract void configureTreeListener();
	
	public TreeItem getSelectedItem() {
		return tree.getSelectedItem();
	}

	public void selectByUserObject(Object userObject) {
		TreeItem item=selectByUserObjectAux(userObject, root);
		if (item != null) 
			setSelectedItem(item);
	}
	
	private TreeItem selectByUserObjectAux(Object userObject, TreeItem raiz) {
		for (int conta=0;conta<raiz.getChildCount();conta++) {
			TreeItem item=raiz.getChild(conta);
			if ( (item.getUserObject()!=null) && (item.getUserObject().equals(userObject)) ) {
				return item;
			}
			if (item.getChildCount()>0) {
				TreeItem resultItem=selectByUserObjectAux(userObject, item);
				if (resultItem!=null) return resultItem;
			} 
		}
		return null;
	}
	
	public void setSelectedItem(TreeItem item) {
		tree.setSelectedItem(item, true);
		tree.ensureSelectedItemVisible();
		scrollIntoView(item.getElement());
	}

	public TreeItem getRoot() {
		return root;
	}
}
