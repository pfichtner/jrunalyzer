package com.github.pfichtner.jrunalyser.base.data;

import java.io.Serializable;

public class DefaultLinkedWayPoint implements LinkedTrackPoint, Serializable {

	private static final long serialVersionUID = -1565017996348212247L;

	private final WayPoint delegate;
	private final Link link;

	private DefaultLinkedWayPoint(WayPoint wayPoint, Link link) {
		this.delegate = wayPoint;
		this.link = link;
	}

	public static DefaultLinkedWayPoint of(WayPoint wayPoint, Link link) {
		return new DefaultLinkedWayPoint(wayPoint, link);
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

	@Override
	public String toString() {
		return "DefaultTrackPointWithLink [delegate=" + this.delegate
				+ ", link=" + this.link + "]";
	}

}
