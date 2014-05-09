package com.github.pfichtner.jrunalyser.base.data;

import java.util.concurrent.TimeUnit;

public interface Speed extends Comparable<Speed> {

	Speed convert(DistanceUnit distanceUnit, TimeUnit timeUnit);

	Pace toPace(TimeUnit timeUnit, DistanceUnit distanceUnit);

	DistanceUnit getDistanceUnit();

	TimeUnit getTimeUnit();

	double getValue(DistanceUnit distanceUnit, TimeUnit timeUnit);

}