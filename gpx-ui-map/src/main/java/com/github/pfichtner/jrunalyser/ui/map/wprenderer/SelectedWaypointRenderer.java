package com.github.pfichtner.jrunalyser.ui.map.wprenderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.Link;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Pace;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.stat.StatCalculators;
import com.github.pfichtner.jrunalyser.base.data.track.Tracks;
import com.github.pfichtner.jrunalyser.ui.base.Settings;
import com.github.pfichtner.jrunalyser.ui.base.StaticSettings;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter.Type;
import com.github.pfichtner.jrunalyser.ui.format.DurationFormatter;
import com.github.pfichtner.jrunalyser.ui.format.PaceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.SpeedFormatter;
import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;
import com.github.pfichtner.jrunalyser.ui.map.wp.SelectedWaypoint;

public class SelectedWaypointRenderer extends
		AbstractSegmentBorderWaypointRenderer<SelectedWaypoint> {

	private final DistanceFormatter dif = new DistanceFormatter(Type.SHORT);
	private final DurationFormatter duf = new DurationFormatter(
			com.github.pfichtner.jrunalyser.ui.format.DurationFormatter.Type.SHORT);
	private final SpeedFormatter spf = new SpeedFormatter(
			com.github.pfichtner.jrunalyser.ui.format.SpeedFormatter.Type.SHORT);
	private final PaceFormatter paf = new PaceFormatter(
			com.github.pfichtner.jrunalyser.ui.format.PaceFormatter.Type.SHORT);

	public SelectedWaypointRenderer(Theme theme, Font font) {
		super(theme, font);
	}

	@Override
	protected void drawCircleBorder(Graphics2D g, SelectedWaypoint waypoint,
			int r) {
		g.setColor(Color.RED);
		g.fill(new Ellipse2D.Double(-r, -r, 2 * r + 1, 2 * r + 1));

//		WayPoint wp = waypoint.getSelectedWayPoint();
//		Long time = wp.getTime();
//		if (time != null) {
//			if (wp instanceof LinkedTrackPoint) {
//				LinkedTrackPoint ltp = (LinkedTrackPoint) wp;
//				Link link = ltp.getLink();
//
//				Iterable<? extends LinkedTrackPoint> linkedTps = Tracks.fromTo(
//						Tracks.getStartPoint(waypoint.getTrack()),
//						waypoint.getSelectedWayPoint());
//				Distance distance = StatCalculators.distance(
//						DistanceUnit.METERS).calculate(linkedTps);
//				Duration duration = StatCalculators.duration(TimeUnit.SECONDS)
//						.calculate(linkedTps);
//
//				Speed speed = link.getSpeed();
//				Pace pace = speed.toPace(TimeUnit.MINUTES,
//						DistanceUnit.KILOMETERS);
//				Integer elevation = wp.getElevation();
//				Settings settings = StaticSettings.INSTANCE;
//
//				String str = this.dif.format(distance) + "\n"
//						+ this.duf.format(duration) + "\n"
//						+ this.spf.format(settings, speed) + "\n"
//						+ this.paf.format(settings, pace) + "\n" + elevation;
//				drawText(g, str, 100);
//
//			} else
//				g.drawString(new Date(time.longValue()).toString(), 20, -20);
//		}
	}

	private void drawText(Graphics2D g2, String text, float width) {
		FontRenderContext frc = g2.getFontRenderContext();

		AttributedString styledText = new AttributedString(text);
		AttributedCharacterIterator aci = styledText.getIterator();
		int start = aci.getBeginIndex();
		int end = aci.getEndIndex();
		LineBreakMeasurer measurer = new LineBreakMeasurer(aci, frc);
		measurer.setPosition(start);

		float x = 0, y = 0;
		while (measurer.getPosition() < end) {
			TextLayout layout = measurer.nextLayout(width);

			y += layout.getAscent();
			float dx = layout.isLeftToRight() ? 0 : width - layout.getAdvance();

			layout.draw(g2, x + dx, y);
			y += layout.getDescent() + layout.getLeading();
		}
	}

}
