package com.github.pfichtner.jrunalyser.ui.cal.swing.model;

import java.util.Date;

import javax.swing.Icon;

public interface CalendarEntry {

	String getDescription();

	Date getStartDate();

	Date getEndDate();

	Icon getIcon();
	
	Object getUserObject();

}
