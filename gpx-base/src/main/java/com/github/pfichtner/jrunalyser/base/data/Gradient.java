package com.github.pfichtner.jrunalyser.base.data;

public interface Gradient extends MathObject<Gradient> {

	DistanceUnit getDistanceUnit();

	double getValue();

	Gradient convertTo(DistanceUnit targetUnit);

}
