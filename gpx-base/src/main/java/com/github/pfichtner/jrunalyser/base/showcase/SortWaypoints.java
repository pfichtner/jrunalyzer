package com.github.pfichtner.jrunalyser.base.showcase;

import static com.github.pfichtner.jrunalyser.base.util.Validator.validateWaypoints;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxMarshaller;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.segment.DefaultSegment;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public class SortWaypoints {

	public static void main(String[] args) throws IOException {
		File file = new File("/home/xck10h6/", "test.gpx");

		Track track = GpxUnmarshaller.loadTrack(file);
		List<? extends LinkedTrackPoint> sortedCopy = validateWaypoints(createOrdering()
				.sortedCopy(track.getTrackpoints()));
		Track sorted = new DefaultTrack(track.getId(), track.getMetadata(),
				track.getWaypoints(),
				Collections.singletonList(new DefaultSegment(sortedCopy,
						DefaultStatistics.ofWaypoints(sortedCopy))), track.getStatistics());

		GpxMarshaller.writeTrack(file, sorted);
	}

	private static Ordering<LinkedTrackPoint> createOrdering() {
		return Ordering.natural().onResultOf(
				new Function<LinkedTrackPoint, Long>() {
					@Override
					public Long apply(LinkedTrackPoint wp) {
						return wp.getTime();
					}
				});
	}

}
