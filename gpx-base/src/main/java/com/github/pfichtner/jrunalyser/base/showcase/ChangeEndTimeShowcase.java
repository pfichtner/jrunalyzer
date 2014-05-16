package com.github.pfichtner.jrunalyser.base.showcase;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import com.github.pfichtner.jrunalyser.base.data.DefaultLinkedWayPoint;
import com.github.pfichtner.jrunalyser.base.data.DefaultWayPoint;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxMarshaller;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.segment.DefaultSegment;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ChangeEndTimeShowcase {

	private final static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) throws IOException, ParseException {
		Track track = loadTrack(new File(args[0]));
		long origStartTime = Iterables.get(track.getTrackpoints(), 0).getTime()
				.longValue();
		long origEndTime = Iterables.getLast(track.getTrackpoints()).getTime()
				.longValue();
		System.out.println("Orig. end time is "
				+ sdf.format(new Date(origEndTime)));

		long newEndTime = sdf.parse(args[1]).getTime();
		long newDuration = newEndTime - origStartTime;
		double multiplier = ((double) newDuration)
				/ (origEndTime - origStartTime);

		GpxMarshaller.writeTrack(
				new PrintWriter(System.out),
				new DefaultTrack(track.getId(), track.getMetadata(), track
						.getWaypoints(), Collections
						.singletonList(new DefaultSegment(Lists.transform(
								track.getTrackpoints(),
								changeTime(origStartTime, multiplier)), null)),
						null));
	}

	private static Function<LinkedTrackPoint, LinkedTrackPoint> changeTime(
			final long startTime, final double multiplier) {
		return new Function<LinkedTrackPoint, LinkedTrackPoint>() {
			@Override
			public LinkedTrackPoint apply(LinkedTrackPoint in) {
				long newTime = (long) (startTime + (in.getTime().longValue() - startTime)
						* multiplier);
				return DefaultLinkedWayPoint.of(
						new DefaultWayPoint(in.getLatitude(),
								in.getLongitude(), in.getElevation(), Long
										.valueOf(newTime)), in.getLink(), in
								.getOverallDistance(), in.getOverallDuration());
			}
		};
	}

	private static Track loadTrack(File file) throws IOException {
		return GpxUnmarshaller.loadTrack(file);
	}

}
