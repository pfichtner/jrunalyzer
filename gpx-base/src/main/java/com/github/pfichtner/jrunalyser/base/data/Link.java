package com.github.pfichtner.jrunalyser.base.data;

public interface Link {

	int getElevationDifference();

	Distance getDistance();

	Duration getDuration();

	Speed getSpeed();
	
	Gradient getGradient();

	WayPoint getNext();

}
