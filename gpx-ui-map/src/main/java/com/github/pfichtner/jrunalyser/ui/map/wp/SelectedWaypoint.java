package com.github.pfichtner.jrunalyser.ui.map.wp;

import org.jdesktop.swingx.mapviewer.DefaultWaypoint;

import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Track;

public class SelectedWaypoint extends DefaultWaypoint {

	private WayPoint selectedWayPoint;
	private Track track;

	public WayPoint getSelectedWayPoint() {
		return this.selectedWayPoint;
	}

	public Track getTrack() {
		return this.track;
	}

	public void setSelectedWayPoint(Track track, WayPoint selectedWayPoint) {
		this.track = track;
		this.selectedWayPoint = selectedWayPoint;
	}

}
