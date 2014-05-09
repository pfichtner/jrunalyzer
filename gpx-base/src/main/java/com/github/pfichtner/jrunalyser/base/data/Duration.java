package com.github.pfichtner.jrunalyser.base.data;

import java.util.concurrent.TimeUnit;

public interface Duration extends MathObject<Duration>, SegmentationUnit {

	TimeUnit getTimeUnit();

	double getValue(TimeUnit timeUnit);

	Duration convertTo(TimeUnit targetUnit);

}
