package com.github.pfichtner.jrunalyser.ui.map.wprenderer;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;

public abstract class AbstractSegmentBorderWaypointRenderer<T extends Waypoint>
		implements WaypointRenderer {

	protected final Theme theme;
	protected final Font font;

	private final BasicStroke stroke = new BasicStroke(1);

	public AbstractSegmentBorderWaypointRenderer(Theme theme, Font font) {
		this.theme = theme;
		this.font = font;
	}

	@Override
	public boolean paintWaypoint(Graphics2D g, JXMapViewer jxMapViewer,
			Waypoint waypoint) {
		int r = this.font.getSize() - 2;
		@SuppressWarnings("unchecked")
		T casted = (T) waypoint;
		drawCircleBackground(g, casted, r);
		drawCircleBorder(g, casted, r - 2);

		return false;
	}

	protected void drawCircleBackground(Graphics2D g, T waypoint, int r) {
		g.setColor(this.theme.getBgColor());
		g.setStroke(this.stroke);
		g.fill(new Ellipse2D.Double(-r, -r, 2 * r + 1, 2 * r + 1));
	}

	protected void drawCircleBorder(Graphics2D g, T waypoint, int r) {
		g.setColor(this.theme.getFgColor());
		g.setStroke(this.stroke);
		g.draw(new Ellipse2D.Double(-r, -r, 2 * r + 1, 2 * r + 1));
	}

}
