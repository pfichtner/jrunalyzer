package com.github.pfichtner.jrunalyser.ui.mapprofile.config;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;

import java.math.BigDecimal;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.stat.Predicates;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Function;

public class DefaultDatasetConfig implements DatasetConfig {

	public static class Builder {

		private final int index;
		private String description;
		private Function<LinkedTrackPoint, ? extends Number> yFunc;
		private XYItemRenderer renderer;

		public Builder(int index) {
			this.index = index;
		}

		public DefaultDatasetConfig.Builder description(String description) {
			this.description = description;
			return this;
		}

		public DefaultDatasetConfig.Builder yFunc(
				Function<LinkedTrackPoint, ? extends Number> yFunc) {
			this.yFunc = yFunc;
			return this;
		}

		public DefaultDatasetConfig.Builder renderer(XYItemRenderer renderer) {
			this.renderer = renderer;
			return this;
		}

		public DefaultDatasetConfig build() {
			return new DefaultDatasetConfig(this);
		}

	}

	private final int index;
	private final String description;
	private final Function<LinkedTrackPoint, ? extends Number> yFunc;
	private final XYItemRenderer renderer;

	private DefaultDatasetConfig(Builder builder) {
		checkArgument(builder.index >= 0);
		this.index = builder.index;
		this.description = checkNotNull(builder.description);
		this.yFunc = checkNotNull(builder.yFunc);
		this.renderer = checkNotNull(builder.renderer);
	}

	public int getIndex() {
		return this.index;
	}

	public String getDescription() {
		return this.description;
	}

	public XYItemRenderer getRenderer() {
		return this.renderer;
	}

	public NumberAxis createNumberAxis(Track track) {
		return new NumberAxis(this.description);
	}

	public XYDataset createDataset(Track track,
			Function<LinkedTrackPoint, ? extends Number> xFunc) {
		return createDataset(track, xFunc, this.yFunc, this.description);
	}

	private static XYDataset createDataset(Track track,
			Function<LinkedTrackPoint, ? extends Number> xValFunc,
			Function<LinkedTrackPoint, ? extends Number> yValFunc, String key) {
		final XYSeries series = new XYSeries(key, false, false);

		BigDecimal absX = BigDecimal.ZERO;
		for (LinkedTrackPoint tpd : filter(track.getTrackpoints(),
				Predicates.LinkedWayPoints.hasLink())) {
			Number nextX = xValFunc.apply(tpd);
			Number y = yValFunc.apply(tpd);
			if (nextX.doubleValue() > 0.0 && isValid(nextX) && isValid(y)) {
				absX = absX.add(new BigDecimal(nextX.toString()));
				series.add(absX, y);
			}
		}
		return new XYSeriesCollection(series);
	}

	private static boolean isValid(Number value) {
		return !(value instanceof Double)
				|| (!Double.isNaN(value.doubleValue()) && !Double
						.isInfinite(value.doubleValue()));
	}

}