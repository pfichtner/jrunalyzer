package com.github.pfichtner.jrunalyser.ui.cal.swing;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class Chooser extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = -1332243671366383016L;

	private JLabel prev;
	private JLabel next;
	private JLabel actual;
	private final Calendar calendar;

	public Chooser() {
		this(Calendar.getInstance());
	}

	public Chooser(final Calendar calendar) {
		super(new BorderLayout());
		this.calendar = calendar;

		Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		this.prev = new JLabel("<<"); //$NON-NLS-1$
		this.prev.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Date old = calendar.getTime();
				calendar.add(Calendar.MONTH, -1);
				firePropertyChange("monthChanged", old, calendar.getTime()); //$NON-NLS-1$
			}
		});
		this.prev.setBorder(border);

		this.actual = new JLabel();
		this.actual.setBorder(border);
		this.actual.setHorizontalAlignment(SwingConstants.CENTER);

		this.next = new JLabel(">>"); //$NON-NLS-1$
		this.next.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Date old = calendar.getTime();
				calendar.add(Calendar.MONTH, +1);
				firePropertyChange("monthChanged", old, calendar.getTime()); //$NON-NLS-1$
			}
		});
		this.next.setBorder(border);

		add(this.prev, BorderLayout.WEST);
		add(this.actual, BorderLayout.CENTER);
		add(this.next, BorderLayout.EAST);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("dateChanged".equals(evt.getPropertyName())) { //$NON-NLS-1$
			this.calendar.setTime((Date) evt.getNewValue());
			int i = this.calendar.get(Calendar.MONTH);
			int y = this.calendar.get(Calendar.YEAR);
			this.actual.setText(getMonthName(i) + " " + y); //$NON-NLS-1$
		}
	}

	private String getMonthName(int i) {
		return DateFormatSymbols.getInstance().getMonths()[(12 + i) % 12];
	}

}
