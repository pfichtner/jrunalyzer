package com.github.pfichtner.jrunalyser.ui.map.painter;

import java.awt.Graphics2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

import com.github.pfichtner.jrunalyser.base.Delegate;

public class OffsetWaypointRendererDelegate implements WaypointRenderer,
		Delegate<WaypointRenderer> {

	private final WaypointRenderer delegate;
	private final int xoffset;
	private final int yoffset;

	@Override
	public WaypointRenderer getDelegate() {
		return this.delegate;
	}

	public OffsetWaypointRendererDelegate(int xoffset, int yoffset,
			WaypointRenderer delegate) {
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		this.delegate = delegate;
	}

	@Override
	public boolean paintWaypoint(Graphics2D g, JXMapViewer mapViewer,
			Waypoint waypoint) {
		g.translate(this.xoffset, this.yoffset);
		boolean result = this.delegate.paintWaypoint(g, mapViewer, waypoint);
		g.translate(-this.xoffset, -this.yoffset);
		return result;
	}

}
