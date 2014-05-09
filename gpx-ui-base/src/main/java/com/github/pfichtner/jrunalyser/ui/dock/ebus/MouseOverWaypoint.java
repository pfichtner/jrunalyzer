package com.github.pfichtner.jrunalyser.ui.dock.ebus;

import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Track;

@EventBusMessage
public class MouseOverWaypoint {

	private final Track track;
	private final WayPoint selectedWayPoint;

	public MouseOverWaypoint(Track track, WayPoint selectedWayPoint) {
		this.track = track;
		this.selectedWayPoint = selectedWayPoint;
	}

	public Track getTrack() {
		return this.track;
	}

	public WayPoint getSelectedWayPoint() {
		return this.selectedWayPoint;
	}

}
