package com.github.pfichtner.jrunalyser.ui.cal;

import java.util.Date;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import com.github.pfichtner.jrunalyser.ui.cal.swing.MyCalendar;

public class Test {

	public static void main(String[] args) {
		JDialog jDialog = new JDialog();

		MyCalendar pane = new MyCalendar();
		pane.setDate(new Date());
		jDialog.add(pane);
		jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jDialog.pack();
		jDialog.setVisible(true);
	}

}
