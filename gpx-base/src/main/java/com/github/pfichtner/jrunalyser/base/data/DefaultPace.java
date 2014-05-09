package com.github.pfichtner.jrunalyser.base.data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.google.common.primitives.Doubles;

public class DefaultPace implements Pace, Serializable {

	private static final long serialVersionUID = -82131255646159944L;

	private final double tPerD;
	private final TimeUnit timeUnit;
	private final DistanceUnit distanceUnit;

	private static final long fix = TimeUnit.MILLISECONDS.convert(1,
			TimeUnit.DAYS);

	public DefaultPace(double tPerD, TimeUnit timeUnit,
			DistanceUnit distanceUnit) {
		this.tPerD = tPerD;
		this.timeUnit = timeUnit;
		this.distanceUnit = distanceUnit;
	}

	protected static DefaultPace of(Speed speed, TimeUnit timeUnit,
			DistanceUnit distanceUnit) {
		return new DefaultPace(1 / speed.getValue(distanceUnit, timeUnit),
				timeUnit, distanceUnit);
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
	public double getValue(TimeUnit timeUnit, DistanceUnit distanceUnit) {
		return convert(timeUnit, distanceUnit).tPerD;
	}

	public DefaultPace convert(TimeUnit timeUnit, DistanceUnit distanceUnit) {
		if (distanceUnit == this.distanceUnit && timeUnit == this.timeUnit) {
			return this;
		}
		long longDist = (long) (fix * this.distanceUnit.convert(this.tPerD,
				distanceUnit));
		return new DefaultPace((double) (timeUnit.convert(longDist,
				this.timeUnit)) / fix, timeUnit, distanceUnit);
	}

	@Override
	public Speed toSpeed(DistanceUnit distanceUnit, TimeUnit timeUnit) {
		return new DefaultSpeed(1 / getValue(timeUnit, distanceUnit),
				distanceUnit, timeUnit);
	}

	@Override
	public String toString() {
		return this.tPerD + " " + this.timeUnit + " per " + this.distanceUnit;
	}

	public int compareTo(Pace o) {
		return 0 - Doubles.compare(getValue(this.timeUnit, this.distanceUnit),
				o.getValue(this.timeUnit, this.distanceUnit));
	}

}
