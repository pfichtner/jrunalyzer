package com.github.pfichtner.jrunalyser.base.data.segmenter;

import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.MathObject;
import com.github.pfichtner.jrunalyser.base.data.segment.DefaultSegment;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class MathObjectSegmenter<T extends MathObject<T>> implements Segmenter {

	private final T targetValue;
	private final T zero;
	private final Function<LinkedTrackPoint, T> dataFunc;

	public MathObjectSegmenter(T targetValue, T zero,
			Function<LinkedTrackPoint, T> dataFunc) {
		this.targetValue = targetValue;
		this.zero = zero;
		this.dataFunc = dataFunc;
	}

	public Track segment(Track track) {
		// TODO We could compare if track's distance (or duration) <
		// targetValue, then we would have to do nothing
		List<? extends LinkedTrackPoint> trkpts = track.getTrackpoints();
		List<Segment> result = Lists.newArrayList();
		T actValue = this.zero;
		int start = 0;
		int end = 0;
		int maxEnd = trkpts.size() - 1;
		for (LinkedTrackPoint trkpt : trkpts) {
			end++;
			if (trkpt.getLink() != null) {
				T next = this.dataFunc.apply(trkpt);
				actValue = actValue.add(next);
				// create new segment if limit is reached (but a segment must
				// contain at least two trackpoints)
				if (actValue.compareTo(this.targetValue) >= 0
						&& end > start + 1 && end < maxEnd) {
					List<? extends LinkedTrackPoint> ntrkpt = trkpts.subList(
							start, end);
					result.add(new DefaultSegment(ntrkpt, DefaultStatistics
							.ofWaypoints(ntrkpt)));
					actValue = actValue.subtract(this.targetValue);
					start = end;
				}
			}
		}
		if (end > start) {
			List<? extends LinkedTrackPoint> ntrkpts = trkpts.subList(start,
					end);
			result.add(new DefaultSegment(ntrkpts, DefaultStatistics
					.ofWaypoints(ntrkpts)));
		}
		return new DefaultTrack(track.getId(), track.getMetadata(),
				track.getWaypoints(), result, track.getStatistics());
	}

}
