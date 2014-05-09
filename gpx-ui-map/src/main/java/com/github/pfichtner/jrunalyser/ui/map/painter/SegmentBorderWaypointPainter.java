package com.github.pfichtner.jrunalyser.ui.map.painter;

import static com.github.pfichtner.jrunalyser.ui.map.util.GeoUtil.toGeoPoint;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.getLast;

import java.util.List;
import java.util.Set;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.stat.Orderings;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.ui.map.wp.SegmentBorderWaypoint;
import com.github.pfichtner.jrunalyser.ui.map.wp.SelectedWaypoint;
import com.github.pfichtner.jrunalyser.ui.map.wp.TrackEndWaypoint;
import com.github.pfichtner.jrunalyser.ui.map.wp.TrackStartWaypoint;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SegmentBorderWaypointPainter extends WaypointPainter<JXMapViewer> {

	private final SegmentationUnit segmentationUnit;

	/**
	 * Creates a new SegmentBorderWaypointPainter. A
	 * SegmentBorderWaypointPainter gets its Waypoints from the segments found
	 * in the passed Track.
	 * 
	 * @param segmentationUnit
	 * 
	 * @param addTrack
	 */
	public SegmentBorderWaypointPainter(Track track,
			SegmentationUnit segmentationUnit) {
		this.segmentationUnit = segmentationUnit;
		setWaypoints(getSegmentBorders(track));
	}

	private Set<Waypoint> getSegmentBorders(Track track) {
		List<Waypoint> waypoints = Lists.newArrayList();
		int cnt = 0;
		for (Segment segment : track.getSegments()) {
			if (cnt == 0) {
				waypoints.add(new TrackStartWaypoint(toGeoPoint(getFirst(
						segment.getTrackpoints(), null))));
			}
			LinkedTrackPoint last = getLast(segment.getTrackpoints());
			if (last != null && last.getLink() != null) {
				waypoints.add(new SegmentBorderWaypoint(toGeoPoint(last
						.getLink().getNext()), ++cnt, this.segmentationUnit));
			} else {
				waypoints.add(new TrackEndWaypoint(toGeoPoint(last)));
			}
		}
		return Sets.newLinkedHashSet(Orderings.classTypeOrdering(
				ImmutableList.<Class<? extends Waypoint>> of(
						SegmentBorderWaypoint.class, TrackStartWaypoint.class,
						TrackEndWaypoint.class, SelectedWaypoint.class))
				.sortedCopy(waypoints));
	}

}
