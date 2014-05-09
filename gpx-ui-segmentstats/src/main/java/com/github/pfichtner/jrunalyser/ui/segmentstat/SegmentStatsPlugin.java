package com.github.pfichtner.jrunalyser.ui.segmentstat;

import javax.swing.JPanel;

import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.di.Inject;
import com.github.pfichtner.jrunalyser.ui.base.AbstractUiPlugin;
import com.github.pfichtner.jrunalyser.ui.base.DefaultGridData;
import com.github.pfichtner.jrunalyser.ui.base.GridData;
import com.github.pfichtner.jrunalyser.ui.base.GridDataProvider;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.SegmentSelectedMessage;
import com.google.common.eventbus.Subscribe;

public class SegmentStatsPlugin extends AbstractUiPlugin implements
		GridDataProvider {

	private static final GridData gridData = new DefaultGridData(0, 1, 1, 3);

	private SegmentStatsPanel panel;

	public SegmentStatsPlugin() {
		this.panel = new SegmentStatsPanel();
	}

	@Override
	public String getTitle() {
		return "Segment Statistik";
	}

	@Override
	public JPanel getPanel() {
		return this.panel;
	}

	@Override
	public GridData getGridData() {
		return gridData;
	}

	@Inject
	public void setDatasourceFascade(DatasourceFascade dsf) {
		this.panel.setDatasourceFascade(dsf);
	}

	@Subscribe
	public void setActiveSegment(SegmentSelectedMessage message) {
		this.panel.setActiveSegment(message);
	}


}
