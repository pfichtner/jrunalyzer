package com.github.pfichtner.jrunalyser.base.showcase;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getLast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Pace;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.StaticFileProvider;
import com.github.pfichtner.jrunalyser.base.data.floater.HighlightableSegment;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenter;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenters;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.stat.Predicates;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Strings;

public class FloaterShowcase {

	public static void main(String[] args) throws IOException {

		Track track = loadTrack();

		calc(track, 400, DistanceUnit.METERS);
		calc(track, 0.5, DistanceUnit.MILES);
		calc(track, 1, DistanceUnit.KILOMETERS);
		calc(track, 1, DistanceUnit.MILES);
		calc(track, 2, DistanceUnit.MILES);
		calc(track, 5, DistanceUnit.KILOMETERS);
		calc(track, 10, DistanceUnit.KILOMETERS);
	}

	private static void calc(Track track, double value,
			DistanceUnit distanceUnit) {

		Segmenter segmenter = Segmenters.floatingDistance(DefaultDistance.of(
				value, distanceUnit));
		HighlightableSegment segment = find(
				filter(segmenter.segment(track).getSegments(),
						HighlightableSegment.class),
				Predicates.HighlightableSegments.isHighligted, null);

		DistanceUnit du = DistanceUnit.KILOMETERS;
		TimeUnit tu = TimeUnit.HOURS;
		NumberFormat nf = NumberFormat.getNumberInstance();
		DateFormat df = new SimpleDateFormat("mm:ss");

		String text = "best " + value + " " + distanceUnit + " result";
		System.out.println(text);
		System.out.println(Strings.repeat("-", text.length()));

		if (segment == null) {
			System.out.println("X");
		} else {
			Statistics statistics = DefaultStatistics.ofSegment(segment);
			Speed avgSpeed = statistics.getAvgSpeed();
			Pace avgPace = avgSpeed.toPace(TimeUnit.MINUTES, du);
			Distance distance = statistics.getDistance();
			Long d1 = Long.valueOf((long) avgPace.getValue(
					TimeUnit.MILLISECONDS, avgPace.getDistanceUnit()));
			Double d2 = Double.valueOf(statistics.getDuration().getValue(
					TimeUnit.MILLISECONDS));
			System.out.println(nf.format(distance.getValue(du))
					+ " "
					+ distance.getDistanceUnit()
					+ " in "
					+ df.format(d2)
					+ ", "
					+ statistics.getAscent()
					+ "m -"
					+ statistics.getDescent()
					+ "m, Speed "
					+ nf.format(avgSpeed.getValue(du, tu))
					+ ", Pace "
					+ df.format(d1)
					+ " "
					+ " starting "
					+ new Date(get(segment.getTrackpoints(), 0).getTime()
							.longValue())
					+ ", ending "
					+ new Date(getLast(segment.getTrackpoints()).getTime()
							.longValue()) + " (including "
					+ segment.getTrackpoints().size() + " elements)");
		}
		System.out.println();
	}

	private static Track loadTrack() throws IOException {
		return GpxUnmarshaller.loadTrack(StaticFileProvider.getFixFile());
	}

}
