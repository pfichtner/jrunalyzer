package com.github.pfichtner.jrunalyser.ui.mapprofile.config;

import org.jfree.data.time.MovingAverage;
import org.jfree.data.xy.XYDataset;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Function;

public class MovingAverageConfigDecorator extends DatasetConfigDelegate {

	private final String suffix;
	private final int periodCount;
	private final int skip;

	public MovingAverageConfigDecorator(DatasetConfig delegate) {
		this(delegate, "", 10, 0); //$NON-NLS-1$
	}

	public MovingAverageConfigDecorator(DatasetConfig delegate, String suffix,
			int periodCount, int skip) {
		super(delegate);
		this.suffix = suffix;
		this.periodCount = periodCount;
		this.skip = skip;
	}

	@Override
	public XYDataset createDataset(Track track,
			Function<LinkedTrackPoint, ? extends Number> xFunc) {

		// source - the source collection.
		// suffix - the suffix added to each source series name to create the
		// corresponding moving average series name.
		// periodCount - the number of periods in the moving average
		// calculation.
		// skip - the number of initial periods to skip.
		XYDataset undecorated = super.createDataset(track, xFunc);
		return MovingAverage.createMovingAverage(undecorated, this.suffix,
				this.periodCount, this.skip);
	}

}
