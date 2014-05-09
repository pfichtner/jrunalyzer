package com.github.pfichtner.jrunalyser.ui.cal.swing.model;

import java.util.Date;

import javax.swing.Icon;

public class DefaultCalendarEntry implements CalendarEntry {

	private String description;

	private Date startDate, endDate;

	private Icon icon;

	private Object userObject;

	public String getDescription() {
		return this.description;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public Icon getIcon() {
		return this.icon;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public Object getUserObject() {
		return this.userObject;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

}
