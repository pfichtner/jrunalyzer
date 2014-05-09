package com.github.pfichtner.jrunalyser.ui.map.painter;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.JXMapViewer;

import com.github.pfichtner.jrunalyser.base.data.floater.HighlightableSegment;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;

/**
 * Simple Painter that draws only {@link HighlightableSegment}s using Theme's
 * highlight color.
 * 
 * @author Peter Fichtner
 */
public class HighlightTrackPainter extends AbstractTrackPainter {

	private final Stroke stroke;

	/**
	 * Creates a new HighlightTrackPainter that draws only
	 * {@link HighlightableSegment}s using Theme's highlight color.
	 */
	public HighlightTrackPainter(Track track, Theme theme, int stroke) {
		super(track, theme);
		this.stroke = new BasicStroke(stroke);
	}

	@Override
	protected void drawSegment(Graphics2D g, Segment segment,
			JXMapViewer jxMapViewer) {
		// ignore all non-highlightable and all non-highlighted segments
		if (segment instanceof HighlightableSegment
				&& ((HighlightableSegment) segment).isHighligted()) {
			super.drawSegment(g, segment, jxMapViewer);
		}
	}

	@Override
	protected void drawWaypoint(Graphics2D g, Point2D p1, Point2D p2) {
		g.setStroke(this.stroke);
		g.setColor(getTheme().getHlColor());
		g.draw(new Line2D.Double(p1, p2));
	}

}
