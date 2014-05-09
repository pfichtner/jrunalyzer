package com.github.pfichtner.jrunalyser.base.data;

import java.io.Serializable;

public class DefaultCoordinate implements Coordinate, Serializable {

	private static final long serialVersionUID = 6898515519217819762L;

	private final double lat;
	private final double lon;

	public DefaultCoordinate(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public static DefaultCoordinate of(Coordinate coordinate) {
		return new DefaultCoordinate(coordinate.getLatitude(),
				coordinate.getLongitude());
	}

	@Override
	public double getLatitude() {
		return this.lat;
	}

	@Override
	public double getLongitude() {
		return this.lon;
	}

	@Override
	public String toString() {
		return String.valueOf(getLatitude()) + '/' + getLongitude();
	}

}
