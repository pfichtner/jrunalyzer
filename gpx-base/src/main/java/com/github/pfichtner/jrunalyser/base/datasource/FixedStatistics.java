package com.github.pfichtner.jrunalyser.base.datasource;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.util.FixedDistance;
import com.github.pfichtner.jrunalyser.base.util.FixedDuration;

public class FixedStatistics implements Statistics {

	private final Statistics delegate;
	private final Distance distance;
	private final Duration duration;

	public FixedStatistics(Duration duration, Statistics delegate) {
		this.duration = duration;
		this.delegate = delegate;
		this.distance = new FixedDuration(duration).getDistance(
				delegate.getDistance(), delegate.getDuration());
	}

	public FixedStatistics(Distance distance, Statistics delegate) {
		this.distance = distance;
		this.delegate = delegate;
		this.duration = new FixedDistance(distance).getDuration(
				delegate.getDistance(), delegate.getDuration());
	}

	@Override
	public Distance getDistance() {
		return this.distance;
	}

	@Override
	public Duration getDuration() {
		return this.duration;
	}

	@Override
	public WayPoint getMinElevation() {
		return this.delegate.getMinElevation();
	}

	@Override
	public WayPoint getMaxElevation() {
		return this.delegate.getMaxElevation();
	}

	@Override
	public int getAscent() {
		return this.delegate.getAscent();
	}

	@Override
	public int getDescent() {
		return this.delegate.getDescent();
	}

	@Override
	public LinkedTrackPoint getMinSpeed() {
		return this.delegate.getMinSpeed();
	}

	@Override
	public LinkedTrackPoint getMaxSpeed() {
		return this.delegate.getMaxSpeed();
	}

	@Override
	public Speed getAvgSpeed() {
		return this.delegate.getAvgSpeed();
	}

}
