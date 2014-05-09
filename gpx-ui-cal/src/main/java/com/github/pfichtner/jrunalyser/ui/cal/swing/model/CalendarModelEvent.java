package com.github.pfichtner.jrunalyser.ui.cal.swing.model;

import java.util.Date;

public class CalendarModelEvent {

	public enum Type {
		INSERTED, REMOVED, UPDATED;
	}

	private final Type type;
	private final Date startDate;
	private final Date endDate;

	public CalendarModelEvent(Type type, Date startDate, Date endDate) {
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Type getType() {
		return this.type;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

}
