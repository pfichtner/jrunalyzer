package com.github.pfichtner.jrunalyser.base.util;

import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Duration;

public final class FixedDistance {

	private final Distance fixedDistance;

	public FixedDistance(Distance fixedDistance) {
		this.fixedDistance = fixedDistance; // e.g. 400m
	}

	public Duration getDuration(Distance sourceDistance, Duration sourceDuration) {
		DistanceUnit du = sourceDistance.getDistanceUnit();
		TimeUnit tu = sourceDuration.getTimeUnit();
		double converted = sourceDuration.getValue(tu)
				/ sourceDistance.getValue(du) * this.fixedDistance.getValue(du);
		return DefaultDuration.of(converted, tu);
	}

}