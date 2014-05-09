package com.github.pfichtner.jrunalyser.base.data;

import java.io.Serializable;

import com.github.pfichtner.jrunalyser.base.data.stat.Functions;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;

public class DefaultGradient implements Gradient, Serializable {

	private static final long serialVersionUID = 6479473476841062802L;

	private static final Ordering<Gradient> orderByDistanceUnit = Ordering
			.natural().onResultOf(Functions.Gradients.distanceUnit);

	private DistanceUnit distanceUnit;
	private double value;

	private DefaultGradient(double value, DistanceUnit distanceUnit) {
		this.value = value;
		this.distanceUnit = distanceUnit;
	}

	public double getValue() {
		return this.value;
	}

	public DistanceUnit getDistanceUnit() {
		return this.distanceUnit;
	}

	public static DefaultGradient of(Distance distance, Distance height) {
		DistanceUnit distanceUnit = height.getDistanceUnit();
		return new DefaultGradient(calc(height.getValue(distanceUnit),
				distance.getValue(distanceUnit)), distanceUnit);
	}

	private static double calc(double height, double distance) {
		return 100 * height / distance;
	}

	public static DefaultGradient of(double value, DistanceUnit distanceUnit) {
		return new DefaultGradient(value, distanceUnit);
	}

	public DefaultGradient convertTo(DistanceUnit distanceUnit) {
		return DefaultGradient.of(
				distanceUnit.convert(getValue(), this.distanceUnit),
				distanceUnit);
	}

	@Override
	public int compareTo(Gradient other) {
		final DistanceUnit smaller = findSmallerUnit(other);
		return Doubles.compare(convertTo(smaller).getValue(),
				other.convertTo(smaller).getValue());
	}

	private DistanceUnit findSmallerUnit(final Gradient other) {
		return orderByDistanceUnit.min(this, other).getDistanceUnit();
	}

	@Override
	public Gradient add(Gradient other) {
		return DefaultGradient.of(this.value + getOtherVal(other),
				this.distanceUnit);
	}

	@Override
	public Gradient subtract(Gradient other) {
		return DefaultGradient.of(this.value - getOtherVal(other),
				this.distanceUnit);
	}

	@Override
	public Gradient divide(double divider) {
		return DefaultGradient.of(this.value / divider, this.distanceUnit);
	}

	@Override
	public Gradient multiply(double multiplier) {
		return DefaultGradient.of(this.value * multiplier, this.distanceUnit);
	}

	private double getOtherVal(Gradient other) {
		return other.convertTo(this.distanceUnit).getValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.distanceUnit == null) ? 0 : this.distanceUnit
						.hashCode());
		long temp;
		temp = Double.doubleToLongBits(this.value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultGradient other = (DefaultGradient) obj;
		if (this.distanceUnit != other.distanceUnit)
			return false;
		if (Double.doubleToLongBits(this.value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DefaultGradient [value=" + this.value + ", distanceUnit="
				+ this.distanceUnit + "]";
	}

}
