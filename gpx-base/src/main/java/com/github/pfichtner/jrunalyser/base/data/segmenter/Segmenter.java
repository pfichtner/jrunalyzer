package com.github.pfichtner.jrunalyser.base.data.segmenter;

import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.DefaultSegment;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public interface Segmenter {

	Segmenter NULL_SEGMENTER = new Segmenter() {
		@Override
		public Track segment(Track track) {
			return track.getSegments().size() <= 1 ? track
					: combineSegments(track);
		}

		private Track combineSegments(Track track) {
			List<LinkedTrackPoint> trkpts = Lists.newArrayList();
			for (Segment segment : track.getSegments()) {
				trkpts.addAll(segment.getTrackpoints());
			}
			return new DefaultTrack(track.getId(), track.getMetadata(),
					track.getWaypoints(), ImmutableList.of(new DefaultSegment(
							trkpts, DefaultStatistics.ofWaypoints(trkpts))),
					track.getStatistics());
		}
	};

	Track segment(Track track);

}
