package com.github.pfichtner.jrunalyser.ui.trackcompare;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.di.Inject;
import com.github.pfichtner.jrunalyser.ui.base.AbstractUiPlugin;
import com.github.pfichtner.jrunalyser.ui.base.GridDataProvider;
import com.github.pfichtner.jrunalyser.ui.base.i18n.I18N;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.AdditionalTracks;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackAdded;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackLoaded;
import com.github.pfichtner.jrunalyser.ui.table.renderers.DurationRenderer;
import com.github.pfichtner.jrunalyser.ui.tracklist.MedalRendererDecorator;
import com.github.pfichtner.jrunalyser.ui.tracklist.TrackRow;
import com.github.pfichtner.jrunalyser.ui.tracklist.TrackTable;
import com.github.pfichtner.jrunalyser.ui.tracklist.TrackTableModel;
import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class TrackComparePlugin extends AbstractUiPlugin implements
		GridDataProvider {

	private static final I18N i18n = I18N
			.builder(TrackComparePlugin.class)
			.withParent(
					com.github.pfichtner.jrunalyser.ui.base.UiPlugins.getI18n())
			.build();

	private final JPanel panel = new JPanel(new BorderLayout());
	private final TrackTable table;

	private DatasourceFascade dsf;

	public TrackComparePlugin() {
		this.table = new TrackTable(new TrackTableModel());
		this.table.setInvisible(TrackTableModel.COL_SIM_COUNT);
		// sort by speed
		this.table.getRowSorter().toggleSortOrder(TrackTableModel.COL_SPEED);
		this.table.setCellRenderer(TrackTableModel.COL_DURATION,
				new MedalRendererDecorator(new DurationRenderer()).reverse());
		this.panel.add(new JScrollPane(this.table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS));
	}

	@Override
	public String getTitle() {
		return i18n
				.getText("com.github.pfichtner.jrunalyser.ui.trackcompare.TrackComparePlugin.title"); //$NON-NLS-1$
	}

	@Override
	public JComponent getPanel() {
		return this.panel;
	}

	@Inject
	public void setEventBus(EventBus eventBus) {
		this.table.addEventBusPoster(eventBus);
	}

	@Inject
	public void setDatasourceFascade(final DatasourceFascade dsf)
			throws IOException {
		this.dsf = dsf;
	}

	@Subscribe
	public void setTrack(TrackLoaded message) throws IOException {
		this.table.setTrack(message.getTrack());
		initTableModel();
		int row = this.table.getTablePosOfTrack(message.getTrack());
		if (row >= 0) {
			this.table.getSelectionModel().setSelectionInterval(row, row);
			this.table
					.scrollRectToVisible(this.table.getCellRect(row, 0, true));
		}
	}

	@Subscribe
	public void addTrack(TrackAdded message) {
		try {
			initTableModel();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Subscribe
	public void setAdditionalTracks(AdditionalTracks message)
			throws IOException {
		this.table.setAdditionalTracks(message.getTracks());
	}

	private void initTableModel() throws IOException {
		TrackTableModel tm = this.table.getModel();
		tm.clear();
		Track active = this.table.getTrack();
		if (active != null) {
			// add the track itself
			tm.addRow(new TrackRow(this.dsf, active.getId()));
			// add all similar tracks
			for (Id id : this.dsf.getSimilarTracks(active.getId())) {
				tm.addRow(new TrackRow(this.dsf, id));
			}
		}
	}

}
