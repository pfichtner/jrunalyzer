package com.github.pfichtner.jrunalyser.base.data;

public interface Distance extends MathObject<Distance>, SegmentationUnit {

	DistanceUnit getDistanceUnit();

	double getValue(DistanceUnit distanceUnit);

	Distance convertTo(DistanceUnit targetUnit);

}
