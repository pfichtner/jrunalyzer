package com.github.pfichtner.jrunalyser.ui.cal.swing;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.github.pfichtner.jrunalyser.ui.cal.swing.model.CalendarEntry;
import com.github.pfichtner.jrunalyser.ui.cal.swing.model.CalendarModelEvent;
import com.github.pfichtner.jrunalyser.ui.cal.swing.model.CalendarModelListener;
import com.github.pfichtner.jrunalyser.ui.cal.swing.model.CalenderModel;

public class MyCalendar extends JPanel implements CalendarModelListener {

	/**
	 * indicates a left-click.
	 */
	public static final String PC_DATE_CHANGED = "dateChanged"; //$NON-NLS-1$

	/**
	 * indicates a right-click.
	 */
	public static final String PC_CONTEXT_MENU = "contextMenu"; //$NON-NLS-1$

	private final SimpleDateFormat yymmdd = new SimpleDateFormat("yyyyMMdd"); //$NON-NLS-1$
	private final SimpleDateFormat mm = new SimpleDateFormat("MM"); //$NON-NLS-1$

	public enum DayType {
		CHOSEN, TODAY, SAME_MONTH;
	}

	private static final long serialVersionUID = -468418125168005375L;

	/**
	 * The first day-of-the-week.
	 */
	private int firstDayOfWeek;

	/**
	 * An array of buttons used to display the days-of-the-month.
	 */
	private DateComponent[] buttons;

	/**
	 * The ordered set of all seven days of a week, beginning with the
	 * 'firstDayOfWeek'.
	 */
	private int[] weekDays;

	private CalenderModel calenderModel;

	private Date chosenDate;

	/**
	 * Constructs a new date chooser panel, using today's date as the initial
	 * selection.
	 */
	public MyCalendar() {
		this(Calendar.getInstance());
	}

	/**
	 * Constructs a new date chooser panel.
	 * 
	 * @param calendar
	 *            the calendar controlling the date.
	 * @param controlPanel
	 *            a flag that indicates whether or not the 'today' button should
	 *            appear on the panel.
	 */
	public MyCalendar(final Calendar calendar) {
		super(new BorderLayout());

		// the default date is today...
		this.firstDayOfWeek = calendar.getFirstDayOfWeek();
		this.weekDays = new int[7];
		for (int i = 0; i < 7; i++) {
			this.weekDays[i] = ((this.firstDayOfWeek + i - 1) % 7) + 1;
		}

		Chooser chooser = new Chooser();
		addPropertyChangeListener(chooser);
		chooser.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("monthChanged".equals(evt.getPropertyName())) { //$NON-NLS-1$
					setDate((Date) evt.getNewValue());
				}
			}
		});

		add(chooser, BorderLayout.NORTH);
		add(getCalendarPanel(), BorderLayout.CENTER);
		setDate(calendar.getTime());
	}

	/**
	 * Sets the date chosen in the panel.
	 * 
	 * @param theDate
	 *            the new date.
	 */
	public void setDate(final Date theDate) {
		Date newDate = new Date(theDate.getTime());
		if (this.chosenDate == null || !equalDates(this.chosenDate, newDate)) {
			Date oldValue = this.chosenDate;
			this.chosenDate = newDate;
			firePropertyChange(PC_DATE_CHANGED, oldValue, this.chosenDate);
			refreshButtons();
		}
	}

	/**
	 * Returns the date selected in the panel.
	 * 
	 * @return the selected date.
	 */
	public Date getDate() {
		return new Date(this.chosenDate.getTime());
	}

	/**
	 * Returns a panel of buttons, each button representing a day in the month.
	 * This is a sub-component of the DatePanel.
	 * 
	 * @return the panel.
	 */
	private JPanel getCalendarPanel() {
		final JPanel panel = new JPanel(new GridLayout(7, 7));
		panel.setBorder(BorderFactory
				.createMatteBorder(1, 0, 0, 1, Color.BLACK));

		final String[] weekDays = DateFormatSymbols.getInstance()
				.getShortWeekdays();

		for (int i = 0; i < this.weekDays.length; i++) {
			JLabel comp = new JLabel(weekDays[this.weekDays[i]],
					SwingConstants.CENTER);
			comp.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0,
					Color.BLACK));
			panel.add(comp);
		}

		MouseListener listener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Date old = getDate();
				if (e.getButton() == MouseEvent.BUTTON1) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(getFirstVisibleDate());
					cal.add(Calendar.DATE,
							((DateComponent) e.getSource()).getNum());
					setDate(cal.getTime());
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					firePropertyChange(PC_CONTEXT_MENU, old, getDate());
				}
			}
		};

		this.buttons = new DateComponent[42];
		for (int i = 0; i < this.buttons.length; i++) {
			DateComponent dc = new DateComponent(i);
			dc.addMouseListener(listener);
			panel.add(dc);
			this.buttons[i] = dc;
		}
		return panel;
	}

	@Override
	public synchronized void addMouseListener(MouseListener l) {
		for (DateComponent dateComponent : this.buttons) {
			dateComponent.addMouseListener(l);
		}
	}

	@Override
	public synchronized void removeMouseListener(MouseListener l) {
		for (DateComponent dateComponent : this.buttons) {
			dateComponent.removeMouseListener(l);
		}
	}

	private void renderButon(final Date date, final DateComponent dc) {
		if (this.calenderModel != null) {
			Collection<CalendarEntry> entries = this.calenderModel
					.getEntries(date);
			ImageIcon icon = entries.isEmpty() ? null : new ImageIcon(
					getClass().getResource("/run.gif"));
			dc.setIcon(icon);
			dc.setDisabledIcon(icon);
			dc.setSelected(equalDates(date, this.chosenDate));
			dc.setSelectedMonth(equalMonths(date, this.chosenDate));
			dc.setToday(equalDates(date, new Date()));
		}
	}

	private boolean equalDates(final Date d1, final Date d2) {
		return getYyyyMmDd(d1).equals(getYyyyMmDd(d2));
	}

	private boolean equalMonths(final Date d1, final Date d2) {
		return getMm(d1).equals(getMm(d2));
	}

	private String getYyyyMmDd(final Date date) {
		synchronized (this.yymmdd) {
			return this.yymmdd.format(date);
		}
	}

	private String getMm(final Date date) {
		synchronized (this.mm) {
			return this.mm.format(date);
		}
	}

	/**
	 * Returns the first date that is visible in the grid. This should always be
	 * in the month preceding the month of the selected date.
	 * 
	 * @return the date.
	 */
	private Date getFirstVisibleDate() {
		final Calendar c = Calendar.getInstance();
		c.setTime(this.chosenDate);
		c.set(Calendar.DAY_OF_MONTH, 1);
		while (c.get(Calendar.DAY_OF_WEEK) != getFirstDayOfWeek()) {
			c.add(Calendar.DATE, -1);
		}
		return c.getTime();
	}

	/**
	 * Returns the first day of the week (controls the labels in the date
	 * panel).
	 * 
	 * @return the first day of the week.
	 */
	private int getFirstDayOfWeek() {
		return this.firstDayOfWeek;
	}

	/**
	 * Update the button labels and colors to reflect date selection.
	 */
	private void refreshButtons() {
		Calendar c = Calendar.getInstance();
		c.setTime(getFirstVisibleDate());
		for (int i = 0; i < this.buttons.length; i++) {
			final DateComponent dc = this.buttons[i];
			dc.setDate(c.getTime().getTime());
			renderButon(c.getTime(), dc);
			c.add(Calendar.DATE, 1);
		}
	}

	public void setCalenderModel(CalenderModel calenderModel) {
		if (this.calenderModel != null) {
			this.calenderModel.removeModelListener(this);
		}
		this.calenderModel = checkNotNull(calenderModel);
		this.calenderModel.addModelListener(this);
		refreshButtons();
	}

	@Override
	public void modelChanged(CalendarModelEvent e) {
		if (isVisible(e.getStartDate()) || isVisible(e.getEndDate())) {
			refreshButtons();
		}
	}

	private boolean isVisible(Date date) {
		Date c = getFirstVisibleDate();
		if (date.before(c)) {
			return false;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(c);
		cal.add(Calendar.DAY_OF_MONTH, this.buttons.length);
		return !date.after(cal.getTime());
	}

}
