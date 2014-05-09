package com.github.pfichtner.jrunalyser.ui.mapprofile.config;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Function;

public interface DatasetConfig {

	int getIndex();

	String getDescription();

	XYItemRenderer getRenderer();

	NumberAxis createNumberAxis(Track track);

	XYDataset createDataset(Track track,
			Function<LinkedTrackPoint, ? extends Number> xFunc);

}