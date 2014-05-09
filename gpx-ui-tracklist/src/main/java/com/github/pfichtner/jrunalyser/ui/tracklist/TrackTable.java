package com.github.pfichtner.jrunalyser.ui.tracklist;

import static com.github.pfichtner.jrunalyser.ui.tracklist.TrackListPlugin.getI18n;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.removeIf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.AdditionalTracks;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.HighlightSegmentMessage;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackLoaded;
import com.github.pfichtner.jrunalyser.ui.table.renderers.DistanceRenderer;
import com.github.pfichtner.jrunalyser.ui.table.renderers.DurationRenderer;
import com.github.pfichtner.jrunalyser.ui.table.renderers.SpeedRenderer;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

public class TrackTable extends JTable {

	private static final long serialVersionUID = 8880316446039026151L;

	private Track track;
	private List<Track> additionalTracks = Lists.newArrayList();

	public TrackTable(TrackTableModel trackTableModel) {
		super(trackTableModel);
		setColumnModel(trackTableModel.getColumnModel());
		setDefaultRenderer(Distance.class, new DistanceRenderer());
		setDefaultRenderer(Speed.class, new SpeedRenderer());
		setDefaultRenderer(Duration.class, new DurationRenderer());
		setAutoCreateRowSorter(false);
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setRowSorter(trackTableModel.createTableRowSorter());
		getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
	}

	public void addEventBusPoster(final EventBus eventBus) {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				final TrackTable jTable = (TrackTable) e.getSource();
				final TrackTableModel model = jTable.getModel();
				int idx = jTable.convertRowIndexToModel(jTable.rowAtPoint(e
						.getPoint()));
				if (e.getButton() == MouseEvent.BUTTON3) {
					TrackRow trackRow = model.getRowAt(idx);
					final Track track = trackRow.getTrack();
					JPopupMenu popup = new JPopupMenu();
					popup.add(createOverlayItem(eventBus, jTable, track));
					popup.show(e.getComponent(), e.getX(), e.getY());
				} else if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					Track track = model.getRowAt(idx).getTrack();
					eventBus.post(new TrackLoaded(track));
					int col = jTable.columnAtPoint(e.getPoint());
					Object column = jTable.getTableHeader().getColumnModel()
							.getColumn(col).getIdentifier();
					if (column instanceof SegmentationUnit) {
						eventBus.post(new HighlightSegmentMessage(
								(SegmentationUnit) column));
					}
				}
			}

			private JCheckBoxMenuItem createOverlayItem(
					final EventBus eventBus, final TrackTable jTable,
					final Track track) {

				final Predicate<Track> isThisTrack = compose(
						equalTo(track.getId()),
						com.github.pfichtner.jrunalyser.base.data.stat.Functions.Tracks.id);
				JCheckBoxMenuItem item = new JCheckBoxMenuItem(
						getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.tracklist.TrackTable.miOverlay.title"), //$NON-NLS-1$
						Iterables.tryFind(jTable.additionalTracks, isThisTrack)
								.isPresent());
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						List<Track> adds = Lists
								.newArrayList(jTable.additionalTracks);
						boolean selected = ((JCheckBoxMenuItem) e.getSource())
								.isSelected();
						if (selected) {
							adds.add(track);
						} else {
							removeIf(adds, isThisTrack);
						}
						eventBus.post(new AdditionalTracks(adds));
					}
				});
				item.setEnabled(jTable.track == null
						|| !jTable.track.getId().equals(track.getId()));
				return item;
			}

		});

	}

	@Override
	public TrackTableModel getModel() {
		return (TrackTableModel) super.getModel();
	}

	public void setTrack(Track track) {
		this.track = track;
		this.additionalTracks.clear();
		int row = getTablePosOfTrack(track);
		if (row >= 0) {
			getSelectionModel().setSelectionInterval(row, row);
			scrollRectToVisible(getCellRect(row, 0, true));
		}
	}

	public Track getTrack() {
		return this.track;
	}

	public int getTablePosOfTrack(Track track) {
		Id id = track.getId();
		TrackTableModel tm = getModel();
		for (int i = 0; i < getRowCount(); i++) {
			int modIdx = convertRowIndexToModel(i);
			TrackRow row = tm.getRowAt(modIdx);
			if (row.getTrack().getId().equals(id)) {
				return i;
			}
		}
		return -1;
	}

	// ----------------------------------------------------------------------------------

	public void setCellRenderer(int modelIdx, TableCellRenderer renderer) {
		this.columnModel.getColumn(modelIdx).setCellRenderer(renderer);
	}

	public void setInvisible(int modelIdx) {
		this.columnModel.removeColumn(this.columnModel.getColumn(modelIdx));
	}

	public void setAdditionalTracks(List<Track> additionalTracks) {
		this.additionalTracks = Lists.newArrayList(additionalTracks);
	}

}
