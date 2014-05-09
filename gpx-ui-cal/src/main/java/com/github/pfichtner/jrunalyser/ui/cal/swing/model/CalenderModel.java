package com.github.pfichtner.jrunalyser.ui.cal.swing.model;

import java.util.Collection;
import java.util.Date;

public interface CalenderModel {

	int getCount();

	CalendarEntry getEntry(int index);

	Collection<CalendarEntry> getEntries(Date date);

	// ---------------------------------------------

	void addModelListener(CalendarModelListener l);

	void removeModelListener(CalendarModelListener l);

	void fireElementsInserted(Date startDate, Date endDate);

	void fireTableChanged(CalendarModelEvent e);

}
