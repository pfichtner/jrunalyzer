package com.github.pfichtner.jrunalyser.base.data;

import java.io.Serializable;

import javax.annotation.Nonnegative;

import com.github.pfichtner.jrunalyser.base.data.stat.Functions;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;

public class DefaultDistance implements Distance, Serializable {

	private static final long serialVersionUID = -6510597074912185860L;

	private static final Ordering<Distance> orderByDistanceUnit = Ordering
			.natural().onResultOf(Functions.Distances.distanceUnit);

	private static final Interner<DefaultDistance> interner = Interners
			.newWeakInterner();

	private final DistanceUnit distanceUnit;

	private final double value;

	private DefaultDistance(@Nonnegative double value, DistanceUnit distanceUnit) {
		this.distanceUnit = distanceUnit;
		this.value = value;
	}

	public static DefaultDistance of(double value, DistanceUnit distanceUnit) {
		return interner.intern(new DefaultDistance(value, distanceUnit));
	}

	@Override
	public DistanceUnit getDistanceUnit() {
		return this.distanceUnit;
	}

	@Override
	public double getValue(DistanceUnit distanceUnit) {
		return convertTo(distanceUnit).value;
	}

	@Override
	public DefaultDistance convertTo(DistanceUnit targetUnit) {
		return targetUnit == this.distanceUnit ? this : DefaultDistance.of(
				targetUnit.convert(this.value, this.distanceUnit), targetUnit);
	}

	private DistanceUnit findSmallerUnit(final Distance other) {
		return orderByDistanceUnit.min(this, other).getDistanceUnit();
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
		DefaultDistance other = (DefaultDistance) obj;
		if (this.distanceUnit != other.distanceUnit)
			return false;
		if (Double.doubleToLongBits(this.value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}

	@Override
	public int compareTo(Distance other) {
		final DistanceUnit smaller = findSmallerUnit(other);
		return Doubles.compare(getValue(smaller), other.getValue(smaller));
	}

	@Override
	public Distance add(Distance other) {
		return DefaultDistance.of(this.value + getOtherVal(other),
				this.distanceUnit);
	}

	@Override
	public Distance subtract(Distance other) {
		return DefaultDistance.of(this.value - getOtherVal(other),
				this.distanceUnit);
	}

	@Override
	public Distance divide(double divider) {
		return DefaultDistance.of(this.value / divider, this.distanceUnit);
	}

	@Override
	public Distance multiply(double multiplier) {
		return DefaultDistance.of(this.value * multiplier, this.distanceUnit);
	}

	private double getOtherVal(Distance other) {
		return other.getValue(this.distanceUnit);
	}

	@Override
	public String toString() {
		return "DefaultDistance [distanceUnit=" + this.distanceUnit
				+ ", value=" + this.value + "]";
	}

}
