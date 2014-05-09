package com.github.pfichtner.jrunalyser.ui.map.painter;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;

/**
 * Simple Painter that draws a line using the Theme's background color from
 * waypoint A to waypoint B
 * 
 * @author Peter Fichtner
 */
public class TrackPainter extends AbstractTrackPainter {

	private final Stroke stroke;

	public TrackPainter(Track track, Theme theme, int stroke) {
		super(track, theme);
		this.stroke = new BasicStroke(stroke);
	}

	@Override
	protected void drawWaypoint(Graphics2D g, Point2D p1, Point2D p2) {
		g.setStroke(this.stroke);
		g.setColor(getTheme().getBgColor());
		g.draw(new Line2D.Double(p1, p2));
	}

}
