package com.github.pfichtner.jrunalyser.ui.map.wprenderer;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DivideTrack;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter.Type;
import com.github.pfichtner.jrunalyser.ui.format.DurationFormatter;
import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;
import com.github.pfichtner.jrunalyser.ui.map.wp.SegmentBorderWaypoint;

public class SegmentBorderWaypointRendererPoint implements WaypointRenderer {

	private final Theme theme;
	private final Font font;
	private final int stroke = 2;

	private final static int locX = 20;
	private final static int locY = -20;
	private final static int add = 5;

	private final DistanceFormatter df = new DistanceFormatter(Type.SHORT);

	private final DurationFormatter du = new DurationFormatter(
			com.github.pfichtner.jrunalyser.ui.format.DurationFormatter.Type.SHORT_SYMBOLS);

	public SegmentBorderWaypointRendererPoint(Theme theme, Font font) {
		this.theme = theme;
		this.font = font;
	}

	@Override
	public boolean paintWaypoint(Graphics2D g, JXMapViewer mapViewer,
			Waypoint wp) {

		SegmentBorderWaypoint waypoint = (SegmentBorderWaypoint) wp;

		drawCircleBackground(g, waypoint, 5);
		drawCircleBorder(g, waypoint, 3);

		g.setColor(this.theme.getBgColor());
		g.setStroke(new BasicStroke(this.stroke + 2));
		g.drawLine(0, 0, locX, locY);

		g.setColor(this.theme.getFgColor());
		g.setStroke(new BasicStroke(this.stroke));
		g.drawLine(0, 0, locX, locY);

		String text = getText(waypoint);

		FontRenderContext frc = g.getFontRenderContext();
		TextLayout layout = new TextLayout(text, this.font, frc);
		Rectangle2D bounds = layout.getBounds();

		g.setColor(this.theme.getFgColor());
		drawRect(g, bounds, add + 1);

		// g.setComposite(AlphaComposite
		// .getInstance(AlphaComposite.SRC_OVER, 0.5F));

		g.setColor(this.theme.getBgColor());
		drawRect(g, bounds, add);

		g.setColor(this.theme.getFgColor());
		layout.draw(g, locX, locY);

		return false;
	}

	private void drawRect(Graphics2D g, Rectangle2D bounds, int add) {
		g.fill(new RoundRectangle2D.Float((float) (bounds.getX()) + locX - add,
				(float) (bounds.getY() + locY - add), (float) (bounds
						.getWidth()) + 2 * add, (float) (bounds.getHeight())
						+ 2 * add, 10, 10));
	}

	private String getText(SegmentBorderWaypoint waypoint) {
		SegmentationUnit su = waypoint.getSegmentationUnit();

		// TODO formatting
		// TODO getText for units via ResourceMap

		if (su instanceof Distance) {
			Distance nd = ((Distance) su).multiply(waypoint.getCnt());
			return this.df.format(nd);
		} else if (su instanceof Duration) {
			Duration nd = ((Duration) su).multiply(waypoint.getCnt());
			return this.du.format(nd);
		} else if (su instanceof DivideTrack) {
			DivideTrack divideTrack = (DivideTrack) su;
			return waypoint.getCnt() + "/" + divideTrack.getParts();
		} else {
			return waypoint.getCnt() + "[???" + su.getClass() + "???]";
		}
	}

	private void drawCircleBackground(Graphics2D g,
			SegmentBorderWaypoint waypoint, int r) {
		g.setColor(this.theme.getBgColor());
		g.fill(new Ellipse2D.Double(-r, -r, 2 * r + 1, 2 * r + 1));
	}

	private void drawCircleBorder(Graphics2D g, SegmentBorderWaypoint waypoint,
			int r) {
		g.setColor(this.theme.getFgColor());
		g.draw(new Ellipse2D.Double(-r, -r, 2 * r + 1, 2 * r + 1));
	}

}
