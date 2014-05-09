package com.github.pfichtner.jrunalyser.ui.dock.ebus;

@EventBusMessage
public class PositionSelected {

	private final double lat;
	private final double lng;

	public PositionSelected(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public double getLat() {
		return this.lat;
	}

	public double getLng() {
		return this.lng;
	}

}
