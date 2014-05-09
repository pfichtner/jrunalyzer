package com.github.pfichtner.jrunalyser.ui.overallstats;

import java.io.IOException;

import javax.swing.JPanel;

import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.di.Inject;
import com.github.pfichtner.jrunalyser.ui.base.AbstractUiPlugin;
import com.github.pfichtner.jrunalyser.ui.base.GridDataProvider;
import com.github.pfichtner.jrunalyser.ui.base.i18n.I18N;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackAdded;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackRemoved;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class OverallStatsPlugin extends AbstractUiPlugin implements
		GridDataProvider {

	private OverallStatsPanel panel;

	private static final I18N i18n = I18N
			.builder(OverallStatsPlugin.class)
			.withParent(
					com.github.pfichtner.jrunalyser.ui.base.UiPlugins.getI18n())
			.build();

	public static I18N getI18n() {
		return i18n;
	}

	public OverallStatsPlugin() {
		this.panel = new OverallStatsPanel();
	}

	@Override
	public String getTitle() {
		return i18n
				.getText("com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPlugin.title"); //$NON-NLS-1$
	}

	@Override
	public JPanel getPanel() {
		return this.panel;
	}

	@Inject
	public void setEventBus(EventBus eventBus) {
		this.panel.addEventBusPoster(eventBus);
	}

	@Inject
	public void setDatasourceFascade(DatasourceFascade dsf) throws IOException {
		this.panel.setDatasourceFascade(dsf);
		this.panel.initialize();
	}

	@Subscribe
	public void setTrackAdded(TrackAdded trackAdded) throws IOException {
		this.panel.initialize();
	}

	@Subscribe
	public void setTrackAdded(TrackRemoved trackRemoved) throws IOException {
		this.panel.initialize();
	}

}
