package com.github.pfichtner.jrunalyser.base.data;

import java.util.concurrent.TimeUnit;

public interface Pace extends Comparable<Pace> {

	Pace convert(TimeUnit timeUnit, DistanceUnit distanceUnit);

	Speed toSpeed(DistanceUnit distanceUnit, TimeUnit timeUnit);

	TimeUnit getTimeUnit();
	
	DistanceUnit getDistanceUnit();

	double getValue(TimeUnit timeUnit, DistanceUnit distanceUnit);
	
}