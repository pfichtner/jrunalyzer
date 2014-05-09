package com.github.pfichtner.jrunalyser.base.data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.google.common.math.LongMath;
import com.google.common.primitives.Doubles;

public class DefaultSpeed implements Speed, Serializable {

	private static final long serialVersionUID = 1609904844575774284L;

	private static final long fix = TimeUnit.MILLISECONDS.convert(1,
			TimeUnit.DAYS);

	private final double dPerT;
	private final DistanceUnit distanceUnit;
	private final TimeUnit timeUnit;

	public DefaultSpeed(Distance distance, Duration duration) {
		this(distance.getValue(distance.getDistanceUnit())
				/ duration.getValue(duration.getTimeUnit()), distance
				.getDistanceUnit(), duration.getTimeUnit());
	}

	public DefaultSpeed(double dPerT, DistanceUnit distanceUnit,
			TimeUnit timeUnit) {
		this.dPerT = dPerT;
		this.distanceUnit = distanceUnit;
		this.timeUnit = timeUnit;
	}

	public DefaultSpeed convert(DistanceUnit distanceUnit, TimeUnit timeUnit) {
		return convert(distanceUnit, 1, timeUnit);
	}

	private DefaultSpeed convert(DistanceUnit distanceUnit, int time,
			TimeUnit timeUnit) {
		if (distanceUnit == this.distanceUnit && timeUnit == this.timeUnit) {
			return this;
		}
		long longTime = LongMath.checkedMultiply(time, fix);
		return new DefaultSpeed(distanceUnit.convert(
				this.dPerT * this.timeUnit.convert(longTime, timeUnit),
				this.distanceUnit) / fix, distanceUnit, timeUnit);
	}

	@Override
	public Pace toPace(TimeUnit timeUnit, DistanceUnit distanceUnit) {
		return new DefaultPace(1 / getValue(distanceUnit, timeUnit), timeUnit,
				distanceUnit);
	}

	@Override
	public DistanceUnit getDistanceUnit() {
		return this.distanceUnit;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}

	@Override
	public double getValue(DistanceUnit distanceUnit, TimeUnit timeUnit) {
		return convert(distanceUnit, timeUnit).dPerT;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(this.dPerT);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((this.distanceUnit == null) ? 0 : this.distanceUnit
						.hashCode());
		result = prime * result
				+ ((this.timeUnit == null) ? 0 : this.timeUnit.hashCode());
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
		DefaultSpeed other = (DefaultSpeed) obj;
		if (Double.doubleToLongBits(this.dPerT) != Double
				.doubleToLongBits(other.dPerT))
			return false;
		if (this.distanceUnit != other.distanceUnit)
			return false;
		if (this.timeUnit != other.timeUnit)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.dPerT + " " + this.distanceUnit + " per " + this.timeUnit;
	}

	public int compareTo(Speed o) {
		return Doubles.compare(getValue(this.distanceUnit, this.timeUnit),
				o.getValue(this.distanceUnit, this.timeUnit));
	}

}
