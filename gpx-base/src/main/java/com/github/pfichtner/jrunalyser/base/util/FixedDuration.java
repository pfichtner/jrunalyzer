package com.github.pfichtner.jrunalyser.base.util;

import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Duration;

public final class FixedDuration {

	private final Duration fixedDuration;

	public FixedDuration(Duration fixedDuration) {
		this.fixedDuration = fixedDuration; // e.g. 1h
	}

	public Distance getDistance(Distance sourceDistance, Duration sourceDuration) {
		DistanceUnit du = sourceDistance.getDistanceUnit();
		TimeUnit tu = sourceDuration.getTimeUnit();
		double converted = sourceDistance.getValue(du)
				/ sourceDuration.getValue(tu) * this.fixedDuration.getValue(tu);
		return DefaultDistance.of(converted, du);
	}

}