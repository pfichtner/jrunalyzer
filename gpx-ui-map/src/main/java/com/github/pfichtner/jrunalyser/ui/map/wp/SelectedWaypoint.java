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

	public void setSelectedWayPoint(WayPoint selectedWayPoint) {
		this.selectedWayPoint = selectedWayPoint;
	}

}
