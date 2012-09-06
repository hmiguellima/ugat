package org.ucp.gwt.widgets;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

public class TooltipTableCellRenderer implements TableCellRenderer {
	private final int visibleChars;
	private String title;
	
	public TooltipTableCellRenderer(String title, int visibleChars) {
		this.visibleChars=visibleChars;
		this.title=title;
	}
	
	public Widget getTableCellRendererComponent(MVCTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		FlowPanel panel=new FlowPanel();
		Image tipImage;
		Label label=new Label();
		String tip=(String)value;
		String[] lines=tip.split("\n");
		int chars;
		String labelCaption;

		label.setStyleName("ucpgwt-TooltipTableCellRenderer-Label");
		panel.add(label);
		if ( (tip.length()>0) && (lines.length>0) ) { 
			if ( (lines[0].length()<visibleChars) && (lines.length==1) )
				label.setText(lines[0]);
			else {
				labelCaption=lines[0].replaceAll("\r", "");
				if (labelCaption.length()<visibleChars)
					chars=labelCaption.length();
				else
					chars=visibleChars;
				labelCaption=labelCaption.substring(0, chars);
				label.setText(labelCaption+"...");
				tipImage=new Image(GWT.getModuleBaseURL()+"img/edit-find.png");
				panel.add(tipImage);
				tipImage.setStyleName("ucpgwt-TooltipTableCellRenderer-TipImage");
				tipImage.addMouseListener(new TooltipMouseListener(panel, true, title, tip));
			}
		}
		panel.setStyleName("ucpgwt-TooltipTableCellRenderer");
		return panel;
	}

	public void setComponentStyle(MVCTable table, Widget component, boolean isSelected, boolean hasFocus, int row, int column) {
	}

}
