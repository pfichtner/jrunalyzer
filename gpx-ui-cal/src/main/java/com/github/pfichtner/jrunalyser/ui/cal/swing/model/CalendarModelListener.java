package com.github.pfichtner.jrunalyser.ui.cal.swing.model;

import java.util.EventListener;

public interface CalendarModelListener extends EventListener {

	void modelChanged(CalendarModelEvent e);

}
