package com.github.pfichtner.jrunalyser.ui.tracklist;

import static com.github.pfichtner.jrunalyser.ui.tracklist.TrackListPlugin.getI18n;
import static com.google.common.collect.Iterables.getFirst;

import java.awt.Component;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Pace;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.ui.table.renderers.DistanceRenderer;
import com.github.pfichtner.jrunalyser.ui.table.renderers.DurationRenderer;
import com.github.pfichtner.jrunalyser.ui.table.renderers.PaceRenderer;
import com.github.pfichtner.jrunalyser.ui.table.renderers.SpeedRenderer;
import com.google.common.collect.Lists;

public class TrackTableModel extends AbstractTableModel {

	private static class ElevationRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setFont(null);
			setText(value == null ? null : ((MinMaxElevation) value)
					.getTextLong());
			setBackground(isSelected ? table.getSelectionBackground() : null);
			return this;
		}

	}

	// TODO Add amount of breaks

	public static final int COL_STARTDATE = 0;
	public static final int COL_NAME = 1;
	public static final int COL_DESC = 2;
	public static final int COL_DURATION = 3;
	public static final int COL_DISTANCE = 4;
	public static final int COL_ELEVATION = 5;

	public static final int COL_SPEED = 6;
	public static final int COL_PACE = 7;

	public static final int COL_SIM_COUNT = 8;

	public static final int COL_400M = 9;
	public static final int COL_12MIN = 10;

	public static final int COL_HALF_MILE = 11;
	public static final int COL_1000M = 12;
	public static final int COL_1MILE = 13;
	public static final int COL_2MILES = 14;
	public static final int COL_5000M = 15;
	public static final int COL_10000M = 16;

	public static final int COL_IS_AWAY_EQ_RETURN = 17;

	private static final long serialVersionUID = 8199955700069773640L;

	private static TableCellRenderer headerRenderer = new TableHeaderRenderer();

	private final List<TrackRow> data = Lists.newArrayList();

	@Override
	public int getRowCount() {
		return this.data.size();
	}

	public TrackRow getRowAt(int idx) {
		return this.data.get(idx);
	}

	@Override
	public int getColumnCount() {
		return 17;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case COL_STARTDATE:
			return Date.class;
		case COL_NAME:
			return String.class;
		case COL_DESC:
			return String.class;
		case COL_DURATION:
			return Duration.class;
		case COL_DISTANCE:
			return Distance.class;
		case COL_ELEVATION:
			return MinMaxElevation.class;
		case COL_SPEED:
			return Speed.class;
		case COL_PACE:
			return Pace.class;
		case COL_SIM_COUNT:
			return Integer.class;
		case COL_400M:
			return Duration.class;
		case COL_12MIN:
			return Distance.class;
		case COL_HALF_MILE:
		case COL_1000M:
		case COL_1MILE:
		case COL_2MILES:
		case COL_5000M:
		case COL_10000M:
			return Duration.class;
		case COL_IS_AWAY_EQ_RETURN:
			return Boolean.class;
		default:
			throw new IllegalStateException("Unknown column " + columnIndex); //$NON-NLS-1$
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Track track = getTrack(rowIndex);
		switch (columnIndex) {
		case COL_STARTDATE:
			LinkedTrackPoint first = getFirst(track.getTrackpoints(), null);
			return first == null ? null : new Date(first.getTime().longValue());
		case COL_NAME:
			return track.getMetadata().getName();
		case COL_DESC:
			return track.getMetadata().getDescription();
		case COL_DURATION:
			return track.getStatistics().getDuration();
		case COL_DISTANCE:
			return track.getStatistics().getDistance();
		case COL_ELEVATION:
			return this.data.get(rowIndex).getMinMaxElevation();
		case COL_SPEED:
		case COL_PACE:
			return track.getStatistics().getAvgSpeed();
		case COL_SIM_COUNT:
			return this.data.get(rowIndex).getSimCount();
		case COL_12MIN:
			return getDistance((Duration) getHeaderValue(columnIndex),
					this.data.get(rowIndex));
		case COL_400M:
		case COL_HALF_MILE:
		case COL_1000M:
		case COL_1MILE:
		case COL_2MILES:
		case COL_5000M:
		case COL_10000M:
			return getDuration((Distance) getHeaderValue(columnIndex),
					this.data.get(rowIndex));
		case COL_IS_AWAY_EQ_RETURN:
			return this.data.get(rowIndex).isAwayEqReturn();
		default:
			throw new IllegalStateException("Unknown column " + columnIndex); //$NON-NLS-1$
		}
	}

	public Object getHeaderValue(int columnIndex) {
		Object headerValue = getColumnModel().getColumn(columnIndex)
				.getHeaderValue();
		return headerValue;
	}

	private static Distance getDistance(Duration duration, TrackRow trackRow) {
		Statistics statistics = trackRow.getBestSegment(duration).orNull();
		return statistics == null ? null : statistics.getDistance();
	}

	private static Duration getDuration(Distance distance, TrackRow trackRow) {
		Statistics statistics = trackRow.getBestSegment(distance).orNull();
		return statistics == null ? null : statistics.getDuration();
	}

	private Track getTrack(int rowIndex) {
		return this.data.get(rowIndex).getTrack();
	}

	public void clear() {
		this.data.clear();
		fireTableDataChanged();
	}

	public void addRow(TrackRow trackRow) {
		int cnt = this.data.size();
		this.data.add(trackRow);
		fireTableRowsInserted(cnt, cnt);
	}

	public void addRows(List<TrackRow> trackRows) {
		int cnt = this.data.size();
		this.data.addAll(trackRows);
		fireTableRowsInserted(cnt, cnt + trackRows.size() - 1);
	}

	public void removeRow(Id id) {
		int cnt = 0;
		for (Iterator<TrackRow> it = this.data.iterator(); it.hasNext();) {
			if (it.next().getTrack().getId().equals(id)) {
				it.remove();
				fireTableRowsDeleted(cnt, cnt);
			} else {
				cnt++;
			}
		}
	}

	// ----------------------------------------------------------------------------------

	public TableColumnModel getColumnModel() {
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		columnModel
				.addColumn(createColumn(
						COL_STARTDATE,
						getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.tracklist.TrackTableModel.colStartdate.title"))); //$NON-NLS-1$
		columnModel
				.addColumn(createColumn(
						COL_NAME,
						getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.tracklist.TrackTableModel.colName.title"))); //$NON-NLS-1$
		columnModel
				.addColumn(createColumn(
						COL_DESC,
						getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.tracklist.TrackTableModel.colDescription.title"))); //$NON-NLS-1$
		columnModel
				.addColumn(createColumn(
						COL_DURATION,
						getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.tracklist.TrackTableModel.colDuration.title"), //$NON-NLS-1$
						new MedalRendererDecorator(new DurationRenderer())));
		columnModel
				.addColumn(createColumn(
						COL_DISTANCE,
						getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.tracklist.TrackTableModel.colDistance.title"), //$NON-NLS-1$
						new MedalRendererDecorator(new DistanceRenderer())));
		columnModel
				.addColumn(createColumn(
						COL_ELEVATION,
						getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.tracklist.TrackTableModel.colElevation.title"), //$NON-NLS-1$
						new MedalRendererDecorator(new ElevationRenderer())));
		columnModel
				.addColumn(createColumn(
						COL_SPEED,
						getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.tracklist.TrackTableModel.colSpeed.title"), //$NON-NLS-1$
						new MedalRendererDecorator(new SpeedRenderer())));
		columnModel
				.addColumn(createColumn(
						COL_PACE,
						getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.tracklist.TrackTableModel.colPace.title"), //$NON-NLS-1$
						new MedalRendererDecorator(new PaceRenderer())));
		columnModel
				.addColumn(createColumn(
						COL_SIM_COUNT,
						getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.tracklist.TrackTableModel.colSimCount.title"))); //$NON-NLS-1$
		columnModel.addColumn(createColumn(COL_400M,
				DefaultDistance.of(400, DistanceUnit.METERS),
				new MedalRendererDecorator(new DurationRenderer()).reverse()));
		columnModel.addColumn(createColumn(COL_12MIN,
				DefaultDuration.of(12, TimeUnit.MINUTES),
				new MedalRendererDecorator(new DistanceRenderer())));
		columnModel.addColumn(createColumn(COL_HALF_MILE,
				DefaultDistance.of(0.5, DistanceUnit.MILES),
				new MedalRendererDecorator(new DurationRenderer()).reverse()));
		columnModel.addColumn(createColumn(COL_1000M,
				DefaultDistance.of(1, DistanceUnit.KILOMETERS),
				new MedalRendererDecorator(new DurationRenderer()).reverse()));
		columnModel.addColumn(createColumn(COL_1MILE,
				DefaultDistance.of(1, DistanceUnit.MILES),
				new MedalRendererDecorator(new DurationRenderer()).reverse()));
		columnModel.addColumn(createColumn(COL_2MILES,
				DefaultDistance.of(2, DistanceUnit.MILES),
				new MedalRendererDecorator(new DurationRenderer()).reverse()));
		columnModel.addColumn(createColumn(COL_5000M,
				DefaultDistance.of(5, DistanceUnit.KILOMETERS),
				new MedalRendererDecorator(new DurationRenderer()).reverse()));
		columnModel.addColumn(createColumn(COL_10000M,
				DefaultDistance.of(10, DistanceUnit.KILOMETERS),
				new MedalRendererDecorator(new DurationRenderer()).reverse()));
		columnModel
				.addColumn(createColumn(
						COL_IS_AWAY_EQ_RETURN,
						getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.tracklist.TrackTableModel.colAwayIsReturn.title"))); //$NON-NLS-1$
		return columnModel;
	}

	private static TableColumn createColumn(int modeIdx, Object value) {
		TableColumn column = new TableColumn(modeIdx);
		column.setHeaderValue(value);
		column.setHeaderRenderer(headerRenderer);
		return column;
	}

	private static TableColumn createColumn(int modelIdx, Object value,
			TableCellRenderer cellRenderer) {
		TableColumn column = createColumn(modelIdx, value);
		column.setCellRenderer(cellRenderer);
		return column;
	}

	// ----------------------------------------------------------------------------------

	public TableRowSorter<TableModel> createTableRowSorter() {
		TableModel tm = this;
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tm);
		// reverse all Durations (shown in Distance columns) (but to the
		// COL_DURATION)
		for (int i = 0; i < tm.getColumnCount(); i++) {
			if (i != COL_DURATION
					&& Duration.class.isAssignableFrom(tm.getColumnClass(i))) {
				reverse(sorter, i);
			}
		}
		return sorter;
	}

	private static TableRowSorter<TableModel> reverse(
			TableRowSorter<TableModel> sorter, int column) {
		sorter.setComparator(column,
				Collections.reverseOrder(sorter.getComparator(column)));
		return sorter;
	}

}