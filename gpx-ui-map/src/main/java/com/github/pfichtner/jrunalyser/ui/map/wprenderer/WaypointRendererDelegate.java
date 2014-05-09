package com.github.pfichtner.jrunalyser.ui.map.wprenderer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Graphics2D;
import java.util.Map;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

import com.google.common.collect.ImmutableMap;

public class WaypointRendererDelegate implements WaypointRenderer {

	private final ImmutableMap<Class<? extends Waypoint>, WaypointRenderer> delegates;

	public WaypointRendererDelegate(
			Map<Class<? extends Waypoint>, ? extends WaypointRenderer> delegates) {
		this.delegates = ImmutableMap.copyOf(delegates);
	}

	@Override
	public boolean paintWaypoint(Graphics2D g, JXMapViewer jxMapViewer,
			Waypoint waypoint) {
		Class<? extends Waypoint> wpClass = waypoint.getClass();
		return checkNotNull(this.delegates.get(wpClass),
				"No painter registered for %s", wpClass).paintWaypoint(g, //$NON-NLS-1$
				jxMapViewer, waypoint);
	}

}
