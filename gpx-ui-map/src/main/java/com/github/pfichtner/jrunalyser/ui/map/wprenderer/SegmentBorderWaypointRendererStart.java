package com.github.pfichtner.jrunalyser.ui.map.wprenderer;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;
import com.github.pfichtner.jrunalyser.ui.map.wp.TrackStartWaypoint;

public class SegmentBorderWaypointRendererStart extends
		AbstractSegmentBorderWaypointRenderer<TrackStartWaypoint> {

	public SegmentBorderWaypointRendererStart(Theme theme, Font font) {
		super(theme, font);
	}

	/*
	 * Instead of the simple circle we paint a dot
	 */
	@Override
	protected void drawCircleBackground(Graphics2D g, TrackStartWaypoint waypoint, int r) {
		int rr = r - 5;
		g.setColor(this.theme.getFgColor());
		g.fill(new Ellipse2D.Double(-rr, -rr, 2 * rr + 2, 2 * rr + 2));
	}

}
