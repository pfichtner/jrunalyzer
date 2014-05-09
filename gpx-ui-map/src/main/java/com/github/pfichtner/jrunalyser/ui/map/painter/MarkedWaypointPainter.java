package com.github.pfichtner.jrunalyser.ui.map.painter;

import static com.github.pfichtner.jrunalyser.ui.map.util.GeoUtil.toGeoPoint;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Set;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultWaypointRenderer;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.ui.map.wp.MarkedWaypoint;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class MarkedWaypointPainter extends WaypointPainter<JXMapViewer> {

	public class MarkedWaypointRenderer implements WaypointRenderer {

		private final DefaultWaypointRenderer delegate = new DefaultWaypointRenderer();

		@Override
		public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint wp) {
			boolean b = this.delegate.paintWaypoint(g, map, wp);
			g.drawString(((MarkedWaypoint) wp).getName(), 15, -15);
			return b;
		}

	}

	public MarkedWaypointPainter(DatasourceFascade dsf, Track track) {
		setWaypoints(ImmutableSet.<Waypoint> builder()
				.addAll(convert(track.getWaypoints()))
				.addAll(convert(dsf.getCommonWaypoints())).build());
		setRenderer(new MarkedWaypointRenderer());
	}

	private Set<Waypoint> convert(Collection<? extends WayPoint> commonWaypoints) {
		Set<Waypoint> waypoints = Sets.newHashSet();
		for (WayPoint wp : commonWaypoints) {
			waypoints.add(convert(wp));
		}
		return waypoints;
	}

	private MarkedWaypoint convert(WayPoint wp) {
		return new MarkedWaypoint(toGeoPoint(wp), wp.getName());
	}

}
