package com.github.pfichtner.jrunalyser.ui.mapprofile.config;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Function;

public class DatasetConfigDelegate implements DatasetConfig {

	private final DatasetConfig delegate;

	public DatasetConfigDelegate(DatasetConfig delegate) {
		this.delegate = delegate;
	}

	public int getIndex() {
		return this.delegate.getIndex();
	}

	public String getDescription() {
		return this.delegate.getDescription();
	}

	public XYItemRenderer getRenderer() {
		return this.delegate.getRenderer();
	}

	public NumberAxis createNumberAxis(Track track) {
		return this.delegate.createNumberAxis(track);
	}

	public XYDataset createDataset(Track track,
			Function<LinkedTrackPoint, ? extends Number> xFunc) {
		return this.delegate.createDataset(track, xFunc);
	}

}
