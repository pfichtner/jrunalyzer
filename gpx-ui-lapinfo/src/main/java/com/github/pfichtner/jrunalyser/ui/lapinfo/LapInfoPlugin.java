package com.github.pfichtner.jrunalyser.ui.lapinfo;

import static com.google.common.collect.Iterables.getLast;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Pace;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.floater.DefaultHighlightableSegment;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenter;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenters;
import com.github.pfichtner.jrunalyser.base.data.stat.CombinedStatistics;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.di.Inject;
import com.github.pfichtner.jrunalyser.ui.base.AbstractUiPlugin;
import com.github.pfichtner.jrunalyser.ui.base.GridDataProvider;
import com.github.pfichtner.jrunalyser.ui.base.i18n.I18N;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.SegmentSelectedMessage;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.SegmentationSelected;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackLoaded;
import com.github.pfichtner.jrunalyser.ui.table.renderers.DistanceRenderer;
import com.github.pfichtner.jrunalyser.ui.table.renderers.DurationRenderer;
import com.github.pfichtner.jrunalyser.ui.table.renderers.PaceRenderer;
import com.github.pfichtner.jrunalyser.ui.table.renderers.PositionRendererDecorator;
import com.github.pfichtner.jrunalyser.ui.table.renderers.SpeedRenderer;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class LapInfoPlugin extends AbstractUiPlugin implements GridDataProvider {

	private static final int COL_NR = 0;
	private static final int COL_LAPTIME = 1;
	private static final int COL_TOTALTIME = 2;
	private static final int COL_DISTANCE = 3;
	private static final int COL_TOTALDISTANCE = 4;
	private static final int COL_SPEED = 5;
	private static final int COL_PACE = 6;

	private static final I18N i18n = I18N
			.builder(LapInfoPlugin.class)
			.withParent(
					com.github.pfichtner.jrunalyser.ui.base.UiPlugins.getI18n())
			.build();

	/**
	 * A PositionRendererDecorator that ignores the latest row if the segment is
	 * incomplete.
	 * 
	 * @author Peter Fichtner
	 */
	private static final class MinMaxRendererDecorator extends
			PositionRendererDecorator {

		public MinMaxRendererDecorator(TableCellRenderer delegate) {
			super(delegate);
		}

		@Override
		public int getRowCount(TableModel tm) {
			int rowCount = super.getRowCount(tm);
			return rowCount > 1
					&& tm instanceof LapInfoTableModel
					&& getLast(
							((LapInfoTableModel) tm).getRowAt(rowCount - 1)
									.getSegment().getTrackpoints()).getLink() == null ? rowCount - 1
					: rowCount;
		}

		@Override
		protected void renderPos(JTable table, Component c, int pos) {
			if (pos == 0) {
				renderBest(c);
			} else if (pos == getRowCount(table.getModel()) - 1) {
				renderWorst(c);
			}
		}

		private void renderBest(Component c) {
			c.setBackground(Color.GREEN);
		}

		private void renderWorst(Component c) {
			c.setBackground(Color.RED);
		}

	}

	public static class InfoRow {

		private final int pos;
		private final Segment segment;
		private final Statistics overallStats;

		public InfoRow(int pos, Segment segment, Statistics overallStats) {
			this.pos = pos;
			this.segment = segment;
			this.overallStats = overallStats;
		}

		public int getPos() {
			return this.pos;
		}

		public Segment getSegment() {
			return this.segment;
		}

		public Statistics getOverallStats() {
			return this.overallStats;
		}

	}

	public static class LapInfoTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 8199955700069773640L;

		private final List<InfoRow> data = Lists.newArrayList();

		@Override
		public int getRowCount() {
			return this.data.size();
		}

		public InfoRow getRowAt(int idx) {
			return this.data.get(idx);
		}

		@Override
		public int getColumnCount() {
			return 7;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case COL_NR:
				return Integer.class;
			case COL_LAPTIME:
				return Duration.class;
			case COL_TOTALTIME:
				return Duration.class;
			case COL_DISTANCE:
				return Distance.class;
			case COL_TOTALDISTANCE:
				return Distance.class;
			case COL_SPEED:
				return Speed.class;
			case COL_PACE:
				return Pace.class;
			default:
				throw new IllegalStateException("Unknown column " + columnIndex); //$NON-NLS-1$
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			InfoRow infoRow = this.data.get(rowIndex);
			Segment segment = infoRow.getSegment();
			switch (columnIndex) {
			case COL_NR:
				return Integer.valueOf(infoRow.getPos() + 1);
			case COL_LAPTIME:
				return segment.getStatistics().getDuration();
			case COL_TOTALTIME:
				return infoRow.getOverallStats().getDuration();
			case COL_DISTANCE:
				return segment.getStatistics().getDistance();
			case COL_TOTALDISTANCE:
				return infoRow.getOverallStats().getDistance();
			case COL_SPEED:
			case COL_PACE:
				return segment.getStatistics().getAvgSpeed();
			default:
				throw new IllegalStateException("Unknown column " + columnIndex); //$NON-NLS-1$
			}
		}

		public void clear() {
			this.data.clear();
			fireTableDataChanged();
		}

		public void addRow(InfoRow trackRow) {
			int row = this.data.size();
			this.data.add(trackRow);
			fireTableRowsInserted(row, row);
		}

	}

	private Segmenter segmenter = Segmenter.NULL_SEGMENTER;
	private Track track;

	private EventBus eventBus;
	private JPanel panel = new JPanel(new BorderLayout());
	private LapInfoTableModel tableModel = new LapInfoTableModel();
	private JTable table;

	public LapInfoPlugin() {
		this.table = new JTable(this.tableModel);
		this.table.setColumnModel(createColumnModel(this.tableModel));
		this.table.setDefaultRenderer(Distance.class, new DistanceRenderer());
		this.table.setDefaultRenderer(Speed.class, new SpeedRenderer());
		this.table.setDefaultRenderer(Duration.class, new DurationRenderer());
		this.table.setAutoCreateRowSorter(true);

		this.table.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				JTable jTable = (JTable) e.getSource();
				LapInfoTableModel model = (LapInfoTableModel) jTable.getModel();
				int idx = jTable.convertRowIndexToModel(jTable.rowAtPoint(e
						.getPoint()));
				Segment segment = model.getRowAt(idx).getSegment();
				List<? extends LinkedTrackPoint> swps = segment
						.getTrackpoints();
				Segment hls = new DefaultHighlightableSegment(swps, true,
						DefaultStatistics.ofWaypoints(swps));
				LapInfoPlugin.this.eventBus.post(new SegmentSelectedMessage(
						idx, hls));
			}

		});

		this.panel.add(new JScrollPane(this.table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}

	private static TableColumnModel createColumnModel(TableModel tm) {
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		columnModel.addColumn(createColumn(COL_NR,
				i18n.getText("com.github.pfichtner.jrunalyser.ui.LapInfoPlugin.colNr.tile"))); //$NON-NLS-1$
		columnModel.addColumn(createColumn(COL_LAPTIME,
				i18n.getText("com.github.pfichtner.jrunalyser.ui.LapInfoPlugin.colLaptime.tile"))); //$NON-NLS-1$
		columnModel.addColumn(createColumn(COL_TOTALTIME,
				i18n.getText("com.github.pfichtner.jrunalyser.ui.LapInfoPlugin.colTotaltime.tile"))); //$NON-NLS-1$
		columnModel.addColumn(createColumn(COL_DISTANCE,
				i18n.getText("com.github.pfichtner.jrunalyser.ui.LapInfoPlugin.colDistance.tile"))); //$NON-NLS-1$
		columnModel.addColumn(createColumn(COL_TOTALDISTANCE,
				i18n.getText("com.github.pfichtner.jrunalyser.ui.LapInfoPlugin.coltotalDistance.tile"))); //$NON-NLS-1$
		columnModel.addColumn(createColumn(COL_SPEED,
				i18n.getText("com.github.pfichtner.jrunalyser.ui.LapInfoPlugin.colSpeed.tile"), //$NON-NLS-1$
				new MinMaxRendererDecorator(new SpeedRenderer())));
		columnModel.addColumn(createColumn(COL_PACE,
				i18n.getText("com.github.pfichtner.jrunalyser.ui.LapInfoPlugin.colPace.tile"), //$NON-NLS-1$
				new MinMaxRendererDecorator(new PaceRenderer())));
		return columnModel;
	}

	private static TableColumn createColumn(int modeIdx, String value) {
		TableColumn column = new TableColumn(modeIdx);
		column.setHeaderValue(value);
		return column;
	}

	private static TableColumn createColumn(int modelIdx, String value,
			TableCellRenderer cellRenderer) {
		TableColumn column = createColumn(modelIdx, value);
		column.setCellRenderer(cellRenderer);
		return column;
	}

	@Override
	public String getTitle() {
		return i18n
				.getText("com.github.pfichtner.jrunalyser.ui.LapInfoPlugin.title"); //$NON-NLS-1$
	}

	@Override
	public JPanel getPanel() {
		return this.panel;
	}

	@Inject
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void setTrack(TrackLoaded message) {
		this.track = message.getTrack();
		fillTable();
	}

	@Subscribe
	public void setSegmenter(SegmentationSelected message) {
		this.segmenter = Segmenters.getSegmenter(message.getSegmentationUnit());
		fillTable();
	}

	private void fillTable() {
		if (this.track != null && this.segmenter != null) {
			this.tableModel.clear();
			int i = 0;
			Statistics overall = null;
			for (Segment segment : this.segmenter.segment(this.track)
					.getSegments()) {
				Statistics next = segment.getStatistics();
				overall = overall == null ? next : DefaultStatistics
						.copyOf(new CombinedStatistics(overall, next));
				this.tableModel.addRow(new InfoRow(i++, segment, overall));
			}
		}
	}
}
