package com.github.pfichtner.jrunalyser.base.data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class DefaultLink implements Link, Serializable {

	private static final long serialVersionUID = 2772636706910732866L;

	private final WayPoint next;
	private final int elevationDifference;
	private final Distance distance;
	private final Duration duration;

	private final Speed speed;
	private final Gradient gradient;

	public DefaultLink(WayPoint actual, WayPoint next, Distance distance,
			Duration duration) {
		this.next = next;
		this.duration = duration;
		this.elevationDifference = next.getElevation() == null
				|| actual.getElevation() == null ? 0 : next.getElevation()
				.intValue() - actual.getElevation().intValue();
		this.distance = distance;
		this.speed = new DefaultSpeed(distance, duration);
		this.gradient = DefaultGradient.of(distance, DefaultDistance.of(
				this.elevationDifference, DistanceUnit.METERS));
	}

	public static DefaultLink of(WayPoint wp1, WayPoint wp2) {
		Distance di = GeoUtil.calcDistance(wp1, wp2);
		Duration du = DefaultDuration.of(wp2.getTime().longValue()
				- wp1.getTime().longValue(), TimeUnit.MILLISECONDS);
		return new DefaultLink(wp1, wp2, di, du);
	}

	public int getElevationDifference() {
		return this.elevationDifference;
	}

	public Distance getDistance() {
		return this.distance;
	}

	public Duration getDuration() {
		return this.duration;
	}

	public Speed getSpeed() {
		return this.speed;
	}

	@Override
	public Gradient getGradient() {
		return this.gradient;
	}

	public WayPoint getNext() {
		return this.next;
	}

	@Override
	public String toString() {
		return "DefaultLink [next=" + System.identityHashCode(this.next)
				+ ", elevationDifference=" + this.elevationDifference
				+ ", distance=" + this.distance + ", duration=" + this.duration
				+ ", speed=" + this.speed + ", gradient=" + this.gradient + "]";
	}

}
