package com.github.pfichtner.jrunalyser.ui.map.painter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DateFormat;
import java.util.Date;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.painter.Painter;

import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.ui.base.Settings;
import com.github.pfichtner.jrunalyser.ui.base.StaticSettings;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.DurationFormatter;
import com.github.pfichtner.jrunalyser.ui.format.DurationFormatter.Type;
import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;
import com.google.common.collect.Iterables;

public class InfoBoxPainter implements Painter<JXMapViewer> {

	private static int add = 5;

	private final Track track;
	private final Font font;
	private final Theme theme;

	public InfoBoxPainter(Track track, Theme theme, Font font) {
		this.track = track;
		this.theme = theme;
		this.font = font;
	}

	public void paint(Graphics2D g, JXMapViewer jxMapViewer, int w, int h) {
		Settings settings = StaticSettings.INSTANCE;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		Statistics stats = this.track.getStatistics();
		String text = DateFormat.getDateInstance()
				.format(new Date(Iterables.get(this.track.getTrackpoints(), 0)
						.getTime().longValue()))
				+ ": " + new DistanceFormatter( //$NON-NLS-1$
						com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter.Type.SHORT)
						.format(stats.getDistance().convertTo(
								settings.getDistanceUnit()))
				+ " " //$NON-NLS-1$
				+ new DurationFormatter(Type.SHORT).format(stats.getDuration()
						.convertTo(settings.getTimeUnit()));
		drawTextBox(g, text, this.font, this.theme.getBgColor(),
				this.theme.getFgColor(), 10, 20);
	}

	private static void drawTextBox(Graphics2D g, String text, Font font,
			Color rectColor, Color textColor, int locX, int locY) {

		FontRenderContext frc = g.getFontRenderContext();
		TextLayout layout = new TextLayout(text, font, frc);
		Rectangle2D bounds = layout.getBounds();

		g.setColor(rectColor);
		drawRect(g, bounds, add, locX, locY);

		g.setColor(textColor);
		layout.draw(g, locX, locY);

	}

	private static void drawRect(Graphics2D g, Rectangle2D bounds, int add,
			float locX, double locY) {
		g.fill(new RoundRectangle2D.Float((float) (bounds.getX()) + locX - add,
				(float) (bounds.getY() + locY - add), (float) (bounds
						.getWidth()) + 2 * add, (float) (bounds.getHeight())
						+ 2 * add, 10, 10));
	}

}
