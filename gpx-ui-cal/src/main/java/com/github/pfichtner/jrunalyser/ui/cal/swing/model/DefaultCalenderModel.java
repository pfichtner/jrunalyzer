package com.github.pfichtner.jrunalyser.ui.cal.swing.model;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

public class DefaultCalenderModel extends AbstractCalenderModel {

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); //$NON-NLS-1$
	private final Multimap<String, CalendarEntry> data = TreeMultimap.create(
			Ordering.natural(), new Comparator<CalendarEntry>() {
				@Override
				public int compare(CalendarEntry o1, CalendarEntry o2) {
					return o1.getStartDate().compareTo(o2.getStartDate());
				}
			});

	@Override
	public int getCount() {
		return this.data.size();
	}

	@Override
	public CalendarEntry getEntry(int index) {
		return Iterables.get(this.data.values(), index);
	}

	@Override
	public Collection<CalendarEntry> getEntries(Date date) {
		return this.data.get(getKey(date));
	}

	private String getKey(Date date) {
		synchronized (this.sdf) {
			return this.sdf.format(date);
		}
	}

	public void addElement(CalendarEntry entry) {
		this.data.put(getKey(entry.getStartDate()), entry);
		fireElementsInserted(entry.getStartDate(), entry.getEndDate());
	}

}
