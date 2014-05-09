package com.github.pfichtner.jrunalyser.ui.trackstat;

import java.io.IOException;

import javax.swing.JPanel;

import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.di.Inject;
import com.github.pfichtner.jrunalyser.ui.base.AbstractUiPlugin;
import com.github.pfichtner.jrunalyser.ui.base.DefaultGridData;
import com.github.pfichtner.jrunalyser.ui.base.GridData;
import com.github.pfichtner.jrunalyser.ui.base.GridDataProvider;
import com.github.pfichtner.jrunalyser.ui.base.i18n.I18N;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackLoaded;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class TrackStatsPlugin extends AbstractUiPlugin implements
		GridDataProvider {

	private static final GridData gridData = new DefaultGridData(0, 1, 1, 3);

	private static final I18N i18n = I18N
			.builder(TrackStatsPlugin.class)
			.withParent(
					com.github.pfichtner.jrunalyser.ui.base.UiPlugins.getI18n())
			.build();

	public static I18N getI18n() {
		return i18n;
	}

	private TrackStatsPanel panel;

	public TrackStatsPlugin() {
		this.panel = new TrackStatsPanel();
	}

	@Override
	public String getTitle() {
		return i18n
				.getText("com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPlugin.title"); //$NON-NLS-1$
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
	public void setEventBus(EventBus eventBus) {
		this.panel.addEventBusPoster(eventBus);
	}

	@Inject
	public void setDatasourceFascade(DatasourceFascade dsf) {
		this.panel.setDatasourceFascade(dsf);
	}

	@Subscribe
	public void setTrack(TrackLoaded message) throws IOException {
		this.panel.setTrack(message);
	}

}
