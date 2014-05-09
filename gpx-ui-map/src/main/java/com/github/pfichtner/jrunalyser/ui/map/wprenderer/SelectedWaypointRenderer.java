package com.github.pfichtner.jrunalyser.ui.map.wprenderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;
import com.github.pfichtner.jrunalyser.ui.map.wp.SelectedWaypoint;

public class SelectedWaypointRenderer extends
		AbstractSegmentBorderWaypointRenderer<SelectedWaypoint> {

	public SelectedWaypointRenderer(Theme theme, Font font) {
		super(theme, font);
	}

	@Override
	protected void drawCircleBorder(Graphics2D g, SelectedWaypoint waypoint, int r) {
		// draw the checked flag
		g.setColor(Color.RED);
		g.fill(new Ellipse2D.Double(-r, -r, 2 * r + 1, 2 * r + 1));
	}

}
