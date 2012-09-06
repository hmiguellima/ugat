package org.ucp.gwt.widgets;

import com.google.gwt.user.client.ui.*;

public class TableLayout extends FlexTable {
	private int row=0;
	private int columnSpan=1;
	
	public TableLayout() {
	}
	
	public void appendRow(Widget w1) {
		setWidget(row, 0, w1);
		getFlexCellFormatter().setColSpan(row, 0, columnSpan);
		row++;
	}

	public void appendRow(Widget w1, Widget w2) {
		setWidget(row, 0, w1);
		setWidget(row, 1, w2);
		if (columnSpan<2)
			columnSpan=2;
		row++;
	}

	public void appendRow(Widget w1, Widget w2, Widget w3) {
		setWidget(row, 0, w1);
		setWidget(row, 1, w2);
		setWidget(row, 2, w3);
		if (columnSpan<3)
			columnSpan=3;
		row++;
	}

	public void appendRow(Widget w1, Widget w2, Widget w3, Widget w4) {
		setWidget(row, 0, w1);
		setWidget(row, 1, w2);
		setWidget(row, 2, w3);
		setWidget(row, 3, w4);
		if (columnSpan<4)
			columnSpan=4;
		row++;
	}

	public void appendRow(Widget w1, Widget w2, Widget w3, Widget w4, Widget w5) {
		setWidget(row, 0, w1);
		setWidget(row, 1, w2);
		setWidget(row, 2, w3);
		setWidget(row, 3, w4);
		setWidget(row, 4, w5);
		if (columnSpan<5)
			columnSpan=5;
		row++;
	}
}
