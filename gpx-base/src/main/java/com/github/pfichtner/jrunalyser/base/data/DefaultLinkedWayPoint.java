package com.github.pfichtner.jrunalyser.base.data;

import java.io.Serializable;

public class DefaultLinkedWayPoint implements LinkedTrackPoint, Serializable {

	private static final long serialVersionUID = -1565017996348212247L;

	private final WayPoint delegate;
	private final Link link;

	private final Distance overallDistance;
	private final Duration overallDuration;

	private DefaultLinkedWayPoint(WayPoint wayPoint, Link link,
			Distance overallDistance, Duration overallDuration) {
		this.delegate = wayPoint;
		this.link = link;
		this.overallDistance = overallDistance;
		this.overallDuration = overallDuration;
	}

	public static DefaultLinkedWayPoint of(WayPoint wayPoint, Link link,
			Distance overallDistance, Duration overallDuration) {
		return new DefaultLinkedWayPoint(wayPoint, link, overallDistance,
				overallDuration);
	}

	@Override
	public String getName() {
		return this.delegate.getName();
	}

	public double getLatitude() {
		return this.delegate.getLatitude();
	}

	public double getLongitude() {
		return this.delegate.getLongitude();
	}

	public Integer getElevation() {
		return this.delegate.getElevation();
	}

	public Long getTime() {
		return this.delegate.getTime();
	}

	public Link getLink() {
		return this.link;
	}

	public Distance getOverallDistance() {
		return this.overallDistance;
	}

	public Duration getOverallDuration() {
		return this.overallDuration;
	}

	@Override
	public String toString() {
		return "DefaultLinkedWayPoint [delegate=" + this.delegate + ", link="
				+ this.link + ", overallDistance=" + this.overallDistance
				+ ", overallDuration=" + this.overallDuration + "]";
	}

}
