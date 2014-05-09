package com.github.pfichtner.jrunalyser.ui.cal;

import static com.google.common.collect.Iterables.transform;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.Tracks;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.di.Inject;
import com.github.pfichtner.jrunalyser.ui.base.AbstractUiPlugin;
import com.github.pfichtner.jrunalyser.ui.base.DefaultGridData;
import com.github.pfichtner.jrunalyser.ui.base.GridData;
import com.github.pfichtner.jrunalyser.ui.base.GridDataProvider;
import com.github.pfichtner.jrunalyser.ui.base.i18n.I18N;
import com.github.pfichtner.jrunalyser.ui.cal.swing.DateComponent;
import com.github.pfichtner.jrunalyser.ui.cal.swing.MyCalendar;
import com.github.pfichtner.jrunalyser.ui.cal.swing.model.AbstractCalenderModel;
import com.github.pfichtner.jrunalyser.ui.cal.swing.model.CalendarEntry;
import com.github.pfichtner.jrunalyser.ui.cal.swing.model.CalendarModelEvent;
import com.github.pfichtner.jrunalyser.ui.cal.swing.model.CalenderModel;
import com.github.pfichtner.jrunalyser.ui.cal.swing.model.DefaultCalendarEntry;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackAdded;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackLoaded;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class CalendarPlugin extends AbstractUiPlugin implements
		GridDataProvider {

	public static class DataSourceCalenderModel extends AbstractCalenderModel {

		private final DatasourceFascade dsf;

		public DataSourceCalenderModel(DatasourceFascade dsf) {
			this.dsf = dsf;
		}

		private final Function<Id, CalendarEntry> createEntry = new Function<Id, CalendarEntry>() {

			@Override
			public CalendarEntry apply(Id id) {
				try {
					return createCalendarEntry(
							DataSourceCalenderModel.this.dsf, id);
				} catch (IOException e) {
					throw Throwables.propagate(e);
				}
			}
		};

		@Override
		public int getCount() {
			try {
				return this.dsf.getTrackIds().size();
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}

		@Override
		public CalendarEntry getEntry(int index) {
			try {
				Iterable<Id> trackIds = this.dsf.getTrackIds(new Date(0),
						new Date(Long.MAX_VALUE));
				Id id = Iterables.get(trackIds, index);
				return createCalendarEntry(this.dsf, id);
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}

		@Override
		public Collection<CalendarEntry> getEntries(Date date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date start = new Date(cal.getTime().getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MILLISECOND, -1);
			Date end = new Date(cal.getTime().getTime());
			try {
				return Sets.newHashSet(transform(
						this.dsf.getTrackIds(start, end), this.createEntry));
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}

	}

	private static final GridData gridData = new DefaultGridData(0, 0, 1, 1);

	private MyCalendar calendar;
	private CalenderModel calenderModel;
	private DatasourceFascade dsf;

	private static final I18N i18n = I18N
			.builder(CalendarPlugin.class)
			.withParent(
					com.github.pfichtner.jrunalyser.ui.base.UiPlugins.getI18n())
			.build();

	public CalendarPlugin() {
		this.calendar = new MyCalendar();
		this.calendar.setDate(new Date());
	}

	@Override
	public String getTitle() {
		return i18n
				.getText("com.github.pfichtner.jrunalyser.ui.cal.CalendarPlugin.title"); //$NON-NLS-1$
	}

	@Override
	public JComponent getPanel() {
		return this.calendar;
	}

	@Override
	public GridData getGridData() {
		return gridData;
	}

	private static DefaultCalendarEntry createCalendarEntry(
			DatasourceFascade dsf, Id trackId) throws IOException {
		Track track = dsf.loadTrack(trackId);
		// TODO Do not fixed type (icon) here
		ImageIcon icon = new ImageIcon(
				CalendarPlugin.class.getResource("/run.gif")); //$NON-NLS-1$
		List<? extends WayPoint> trackpoints = track.getTrackpoints();
		// TODO Filter for trackpoints with dates!
		Date dStart = new Date(Iterables.getFirst(trackpoints, null).getTime()
				.longValue());
		Date dEnd = new Date(Iterables.getLast(trackpoints).getTime()
				.longValue());
		DefaultCalendarEntry calItem = new DefaultCalendarEntry();
		calItem.setUserObject(trackId);
		calItem.setStartDate(dStart);
		calItem.setEndDate(dEnd);
		// TODO Do not use fixed type here
		calItem.setDescription("Running");
		calItem.setIcon(icon);
		return calItem;
	}

	@Inject
	public void setDatasourceFascade(DatasourceFascade dsf) {
		this.dsf = dsf;
		this.calenderModel = new DataSourceCalenderModel(dsf);
		this.calendar.setCalenderModel(this.calenderModel);
		this.calendar.setDate(new Date());
	}

	@Inject
	public void setEventBus(final EventBus eventBus) {
		MouseListener l = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				final DateComponent dc = (DateComponent) evt.getSource();
				if (evt.getButton() == MouseEvent.BUTTON1) {
					Id trackId = getSelectedTrackId(dc.getDate());
					if (trackId != null) {
						eventBus.post(new TrackLoaded(loadTrack(trackId)));
					}
				} else if (evt.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu menu = new JPopupMenu();

					final JMenuItem item = new JMenuItem(
							i18n.getText("com.github.pfichtner.jrunalyser.ui.cal.CalendarPlugin.mDeleteTrack.title")); //$NON-NLS-1$
					item.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent ae) {
							Id trackId = getSelectedTrackId(dc.getDate());
							Track track = loadTrack(trackId);
							if (JOptionPane.showConfirmDialog(
									getPanel(),
									i18n.getText(
											"com.github.pfichtner.jrunalyser.ui.cal.CalendarPlugin.mDeleteTrack.warning.title", //$NON-NLS-1$
											track.getMetadata().getName()),
									item.getText(), JOptionPane.YES_NO_OPTION) == 0) {
								try {
									CalendarPlugin.this.dsf
											.removeTrack(trackId);
								} catch (IOException e) {
									throw new RuntimeException(
											"Error deleting track " + trackId, //$NON-NLS-1$
											e);
								}
							}
						}
					});
					menu.add(item);
					menu.show((Component) evt.getSource(), evt.getX(),
							evt.getY());
				}
			}

			private Track loadTrack(Id trackId) {
				try {
					return CalendarPlugin.this.dsf.loadTrack(trackId);
				} catch (IOException e) {
					throw Throwables.propagate(e);
				}
			}

			private Id getSelectedTrackId(long date) {
				Collection<CalendarEntry> entries = CalendarPlugin.this.calenderModel
						.getEntries(new Date(date));
				// TODO Do not take FIRST track
				return entries.isEmpty() ? null : (Id) Iterables
						.get(entries, 0).getUserObject();
			}

		};
		this.calendar.addMouseListener(l);
	}

	@Subscribe
	// change calendar's date to track's date
	public void setTrack(TrackLoaded message) {
		this.calendar.setDate(new Date(Tracks.getStartPoint(message.getTrack())
				.getTime().longValue()));
	}

	@Subscribe
	public void addTrack(TrackAdded message) {
		Track track = message.getTrack();
		List<? extends LinkedTrackPoint> wps = track.getTrackpoints();
		if (!wps.isEmpty()) {
			Date start = new Date(Iterables.get(wps, 0).getTime().longValue());
			Date end = new Date(Iterables.getLast(wps).getTime().longValue());
			this.calenderModel.fireTableChanged(new CalendarModelEvent(
					CalendarModelEvent.Type.INSERTED, start, end));
		}
	}

}
