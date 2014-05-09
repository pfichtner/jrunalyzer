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
import com.google.common.collect.Lists;

public class RemoveInvalidWaypoints {

	public static void main(String[] args) throws IOException {
		File file = new File("/home/xck10h6/", "test.gpx");

		Track track = GpxUnmarshaller.loadTrack(file);
		List<? extends LinkedTrackPoint> sortedCopy = removeInvalids(track
				.getTrackpoints());
		List<? extends LinkedTrackPoint> vwps = validateWaypoints(sortedCopy);
		Track newTrack = new DefaultTrack(track.getId(), track.getMetadata(),
				track.getWaypoints(),
				Collections.singletonList(new DefaultSegment(vwps,
						DefaultStatistics.ofWaypoints(vwps))), track.getStatistics());
		GpxMarshaller.writeTrack(file, newTrack);
	}

	private static List<? extends LinkedTrackPoint> removeInvalids(
			List<? extends LinkedTrackPoint> wps) {
		List<LinkedTrackPoint> result = Lists.newArrayListWithExpectedSize(wps
				.size());
		LinkedTrackPoint old = null;
		for (LinkedTrackPoint next : wps) {
			if (old == null
					|| old.getTime().longValue() < next.getTime().longValue()) {
				result.add(next);
				old = next;
			}
		}
		return result;
	}

}
