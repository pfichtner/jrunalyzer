package com.github.pfichtner.jrunalyser.base.data.stat;

import com.github.pfichtner.jrunalyser.base.data.DefaultSpeed;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;

public class CombinedStatistics implements Statistics {

	private final Statistics a;
	private final Statistics b;

	public CombinedStatistics(Statistics a, Statistics b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public Distance getDistance() {
		return this.a.getDistance().add(this.b.getDistance());
	}

	@Override
	public Duration getDuration() {
		return this.a.getDuration().add(this.b.getDuration());
	}

	@Override
	public WayPoint getMinElevation() {
		return Orderings.elevationOrdering.min(this.a.getMinElevation(),
				this.b.getMinElevation());
	}

	@Override
	public WayPoint getMaxElevation() {
		return Orderings.elevationOrdering.max(this.a.getMaxElevation(),
				this.b.getMaxElevation());
	}

	@Override
	public int getAscent() {
		return this.a.getAscent() + this.b.getAscent();
	}

	@Override
	public int getDescent() {
		return this.a.getDescent() + this.b.getDescent();
	}

	@Override
	public LinkedTrackPoint getMinSpeed() {
		return Orderings.speedOrdering.min(this.a.getMinSpeed(),
				this.b.getMinSpeed());
	}

	@Override
	public LinkedTrackPoint getMaxSpeed() {
		return Orderings.speedOrdering.max(this.a.getMaxSpeed(),
				this.b.getMaxSpeed());
	}

	@Override
	public Speed getAvgSpeed() {
		Speed aAvgSpeed = this.a.getAvgSpeed();
		return new DefaultSpeed(getDistance(), getDuration()).convert(
				aAvgSpeed.getDistanceUnit(), aAvgSpeed.getTimeUnit());
	}

	public static Statistics of(Iterable<Statistics> statistics) {
		Statistics stats = null;
		for (Statistics next : statistics) {
			stats = stats == null ? next : new CombinedStatistics(stats, next);
		}
		return stats == null ? Statistics.NULL : DefaultStatistics
				.copyOf(stats);
	}

}
