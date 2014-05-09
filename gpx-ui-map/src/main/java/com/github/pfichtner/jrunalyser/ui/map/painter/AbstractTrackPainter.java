package com.github.pfichtner.jrunalyser.ui.map.painter;

import static com.google.common.collect.Iterables.filter;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.List;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.util.GeoUtil;
import org.jdesktop.swingx.painter.Painter;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.stat.Predicates;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;

public abstract class AbstractTrackPainter implements Painter<JXMapViewer> {

	private final Track track;
	private final Theme theme;

	public AbstractTrackPainter(Track track, Theme theme) {
		this.track = track;
		this.theme = theme;
	}

	public Track getTrack() {
		return this.track;
	}

	public Theme getTheme() {
		return this.theme;
	}

	public final void paint(Graphics2D g, JXMapViewer jxMapViewer, int w, int h) {
		Rectangle rect = jxMapViewer.getViewportBounds();
		g.translate(-rect.x, -rect.y);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		drawSegments(g, this.track.getSegments(), jxMapViewer);

		g.translate(rect.x, rect.y);
	}

	protected void drawSegments(Graphics2D g, List<Segment> segments,
			JXMapViewer jxMapViewer) {
		for (Segment segment : segments) {
			drawSegment(g, segment, jxMapViewer);
		}
	}

	protected void drawSegment(Graphics2D g, Segment segment,
			JXMapViewer jxMapViewer) {
		for (LinkedTrackPoint wp : filter(segment.getTrackpoints(),
				Predicates.LinkedWayPoints.hasLink())) {
			drawWaypoint(g, segment, wp, jxMapViewer);
		}
	}

	protected void drawWaypoint(Graphics2D g, Segment segment,
			LinkedTrackPoint wp, JXMapViewer jxMapViewer) {
		drawWaypoint(g, toPoint2D(wp, jxMapViewer),
				toPoint2D(wp.getLink().getNext(), jxMapViewer));
	}

	protected abstract void drawWaypoint(Graphics2D g, Point2D p1, Point2D p2);

	private static Point2D toPoint2D(WayPoint trackPoint,
			JXMapViewer jxMapViewer) {
		return GeoUtil.getBitmapCoordinate(trackPoint.getLatitude(), trackPoint
				.getLongitude(), jxMapViewer.getZoom(), jxMapViewer
				.getTileFactory().getInfo());
	}

}
