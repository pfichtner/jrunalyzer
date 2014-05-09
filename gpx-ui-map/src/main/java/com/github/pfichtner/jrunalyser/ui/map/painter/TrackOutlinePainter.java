package com.github.pfichtner.jrunalyser.ui.map.painter;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;

/**
 * Simple Painter that draws a thick line using the Theme's foreground color
 * from waypoint A to waypoint B
 * 
 * @author Peter Fichtner
 */
public class TrackOutlinePainter extends AbstractTrackPainter {

	private Stroke stroke;

	public TrackOutlinePainter(Track track, Theme theme, int stroke) {
		super(track, theme);
		this.stroke = new BasicStroke(stroke + 2);
	}

	@Override
	protected void drawWaypoint(Graphics2D g, Point2D p1, Point2D p2) {
		Theme theme = getTheme();
		g.setColor(theme.getFgColor());
		g.setStroke(this.stroke);
		g.draw(new Line2D.Double(p1, p2));
	}

}
