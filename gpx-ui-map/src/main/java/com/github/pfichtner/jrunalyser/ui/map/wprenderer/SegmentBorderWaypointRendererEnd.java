package com.github.pfichtner.jrunalyser.ui.map.wprenderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;
import com.github.pfichtner.jrunalyser.ui.map.wp.TrackEndWaypoint;

public class SegmentBorderWaypointRendererEnd extends
		AbstractSegmentBorderWaypointRenderer<TrackEndWaypoint> {

	private final static BufferedImage checkedFlag = createCheckedFlag(3);

	public SegmentBorderWaypointRendererEnd(Theme theme, Font font) {
		super(theme, font);
	}

	@Override
	protected void drawCircleBorder(Graphics2D g, TrackEndWaypoint waypoint, int r) {
		// draw the checked flag
		g.setColor(null);
		g.setPaint(new TexturePaint(checkedFlag, new Rectangle(0, 0,
				checkedFlag.getWidth(), checkedFlag.getHeight())));
		g.fill(new Ellipse2D.Double(-r, -r, 2 * r + 1, 2 * r + 1));
	}

	private static BufferedImage createCheckedFlag(int stroke) {
		BufferedImage image = new BufferedImage(2 * stroke, 2 * stroke,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, stroke, stroke);
		g.fillRect(stroke, stroke, stroke, stroke);
		g.setColor(Color.WHITE);
		g.fillRect(stroke, 0, stroke, stroke);
		g.fillRect(0, stroke, stroke, stroke);
		g.dispose();
		return image;
	}

}
