package com.github.pfichtner.jrunalyser.ui.map.wp;

import org.jdesktop.swingx.mapviewer.DefaultWaypoint;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;

/**
 * Waypoint marking a Segment border.
 * 
 * @author Peter Fichtner
 */
public class SegmentBorderWaypoint extends DefaultWaypoint {

	private final int cnt;

	private final SegmentationUnit segmentationUnit;

	public SegmentBorderWaypoint(GeoPosition geoPoint, int cnt,
			SegmentationUnit segmentationUnit) {
		super(geoPoint);
		this.cnt = cnt;
		this.segmentationUnit = segmentationUnit;
	}

	public int getCnt() {
		return this.cnt;
	}

	public SegmentationUnit getSegmentationUnit() {
		return this.segmentationUnit;
	}

}
