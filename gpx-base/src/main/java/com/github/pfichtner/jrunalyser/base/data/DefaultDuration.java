package com.github.pfichtner.jrunalyser.base.data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;

public class DefaultDuration implements Duration, Serializable {

	private static final long serialVersionUID = -602982443795102202L;

	private final static long fix = TimeUnit.DAYS.toMillis(1);

	private static final Interner<DefaultDuration> interner = Interners
			.newWeakInterner();

	private static Function<Duration, TimeUnit> getTimeUnit = new Function<Duration, TimeUnit>() {
		@Override
		public TimeUnit apply(Duration duration) {
			return duration.getTimeUnit();
		}
	};

	private static final Ordering<Duration> orderByTimeUnit = Ordering
			.natural().onResultOf(getTimeUnit);

	private final TimeUnit timeUnit;

	private final double value;

	private DefaultDuration(double value, TimeUnit timeUnit) {
		this.value = value;
		this.timeUnit = timeUnit;
	}

	public static DefaultDuration of(double value, TimeUnit timeUnit) {
		return interner.intern(new DefaultDuration(value, timeUnit));
	}

	@Override
	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}

	@Override
	public double getValue(TimeUnit timeUnit) {
		return convertTo(timeUnit).value;
	}

	@Override
	public DefaultDuration convertTo(TimeUnit targetUnit) {
		double x = this.value * fix;
		return targetUnit == this.timeUnit ? this : DefaultDuration.of(
				(((double) targetUnit.convert((long) x, this.timeUnit)) / fix),
				targetUnit);
	}

	private TimeUnit findSmallerUnit(final Duration other) {
		return orderByTimeUnit.min(this, other).getTimeUnit();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.timeUnit == null) ? 0 : this.timeUnit.hashCode());
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
		DefaultDuration other = (DefaultDuration) obj;
		if (this.timeUnit != other.timeUnit)
			return false;
		if (Double.doubleToLongBits(this.value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}

	@Override
	public int compareTo(Duration other) {
		final TimeUnit smaller = findSmallerUnit(other);
		return Doubles.compare(getValue(smaller), other.getValue(smaller));
	}

	@Override
	public Duration add(Duration other) {
		return DefaultDuration.of(this.value + getOtherVal(other),
				this.timeUnit);
	}

	@Override
	public Duration subtract(Duration other) {
		return DefaultDuration.of(this.value - getOtherVal(other),
				this.timeUnit);
	}

	@Override
	public Duration divide(double divider) {
		return DefaultDuration.of(this.value / divider, this.timeUnit);
	}

	@Override
	public Duration multiply(double multiplier) {
		return DefaultDuration.of(this.value * multiplier, this.timeUnit);
	}

	private double getOtherVal(Duration other) {
		return other.convertTo(this.timeUnit).getValue(this.timeUnit);
	}

	@Override
	public String toString() {
		return "DefaultDuration[timeUnit=" + this.timeUnit + ", value="
				+ this.value + "]";
	}

}
