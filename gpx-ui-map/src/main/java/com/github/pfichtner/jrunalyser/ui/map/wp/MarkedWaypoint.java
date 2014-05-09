package com.github.pfichtner.jrunalyser.ui.map.wp;

import org.jdesktop.swingx.mapviewer.DefaultWaypoint;
import org.jdesktop.swingx.mapviewer.GeoPosition;

public class MarkedWaypoint extends DefaultWaypoint {

	private String name;

	public MarkedWaypoint(GeoPosition geoPoint, String name) {
		super(geoPoint);
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
