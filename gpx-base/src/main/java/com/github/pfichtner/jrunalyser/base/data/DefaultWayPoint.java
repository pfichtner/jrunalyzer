package com.github.pfichtner.jrunalyser.base.data;

import java.io.Serializable;

public class DefaultWayPoint extends DefaultCoordinate implements WayPoint,
		Serializable {

	private static final long serialVersionUID = -7788601188509730103L;

	private final Integer ele;
	private final Long time;

	private String name;

	public DefaultWayPoint(double lat, double lon, Integer ele, Long time) {
		super(lat, lon);
		this.ele = ele;
		this.time = time;
	}

	public DefaultWayPoint(Coordinate coordinate, Integer ele, Long time) {
		this(coordinate.getLatitude(), coordinate.getLongitude(), ele, time);
	}

	public static DefaultWayPoint of(WayPoint wayPoint) {
		DefaultWayPoint wp = new DefaultWayPoint(wayPoint.getLatitude(),
				wayPoint.getLongitude(), wayPoint.getElevation(),
				wayPoint.getTime());
		wp.setName(wayPoint.getName());
		return wp;
	}

	@Override
	public Integer getElevation() {
		return this.ele;
	}

	@Override
	public Long getTime() {
		return this.time;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "DefaultWayPoint [latitude=" + getLatitude() + ", longitude="
				+ getLongitude() + ", ele=" + this.ele + ", time=" + this.time
				+ "]";
	}

}
