package com.github.pfichtner.jrunalyser.base.showcase;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.Pace;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.StaticFileProvider;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenters;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.Track;

public class Showcase {

	public static void main(String[] args) throws IOException {
		File gpxFile = StaticFileProvider.getFixFile();

		// gets points from a GPX file
		Track track = GpxUnmarshaller.loadTrack(gpxFile);
		DistanceUnit du = DistanceUnit.KILOMETERS;
		TimeUnit tu = TimeUnit.HOURS;

		Track segmented = Segmenters.distance(DefaultDistance.of(1, du))
				.segment(track);

		NumberFormat nf = NumberFormat.getNumberInstance();
		DateFormat df = new SimpleDateFormat("mm:ss");

		int i = 0;
		Speed avgSpeed = segmented.getStatistics().getAvgSpeed();
		Pace avgPace = avgSpeed.toPace(TimeUnit.MINUTES, du);
		Distance distance = segmented.getStatistics().getDistance()
				.convertTo(du);
		Double d = Double.valueOf((long) avgPace.getValue(
				TimeUnit.MILLISECONDS, avgPace.getDistanceUnit()));
		System.out.println(gpxFile.getName() + " "
				+ nf.format(distance.getValue(du)) + " " + du + " +"
				+ segmented.getStatistics().getAscent() + "m -"
				+ segmented.getStatistics().getDescent() + "m, Speed "
				+ nf.format(avgSpeed.getValue(du, tu)) + ", Pace "
				+ df.format(d));

		for (Segment segment : segmented.getSegments()) {
			avgSpeed = segment.getStatistics().getAvgSpeed().convert(du, tu);
			avgPace = avgSpeed.toPace(TimeUnit.MINUTES, du);
			Statistics stats = segment.getStatistics();
			distance = stats.getDistance();
			d = Double.valueOf((long) avgPace.getValue(TimeUnit.MILLISECONDS,
					avgPace.getDistanceUnit()));
			Duration duration = segment.getStatistics().getDuration();
			System.out.println("Segment #" + ++i + " "
					+ nf.format(distance.getValue(du)) + " " + du + " with "
					+ segment.getTrackpoints().size() + " elements +"
					+ stats.getAscent() + "m -" + stats.getDescent() + "m, "
					+ duration.getValue(TimeUnit.SECONDS) + " "
					+ duration.getTimeUnit() + ", Speed "
					+ nf.format(avgSpeed.getValue(du, tu)) + ", Pace "
					+ df.format(d));
		}
	}

}
