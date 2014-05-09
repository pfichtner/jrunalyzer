package com.github.pfichtner.jrunalyser.ui.cal.swing.model;

import java.util.Date;

import javax.swing.event.EventListenerList;

public abstract class AbstractCalenderModel implements CalenderModel {

	private final EventListenerList listenerList = new EventListenerList();

	// -----------------------------------------------------------------

	public void addModelListener(CalendarModelListener l) {
		this.listenerList.add(CalendarModelListener.class, l);
	}

	public void removeModelListener(CalendarModelListener l) {
		this.listenerList.remove(CalendarModelListener.class, l);
	}

	// -----------------------------------------------------------------

	public void fireElementsInserted(Date startDate, Date endDate) {
		fireTableChanged(new CalendarModelEvent(
				CalendarModelEvent.Type.INSERTED, startDate, endDate));
	}

	public void fireTableChanged(CalendarModelEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = this.listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CalendarModelListener.class) {
				((CalendarModelListener) listeners[i + 1]).modelChanged(e);
			}
		}
	}

}
