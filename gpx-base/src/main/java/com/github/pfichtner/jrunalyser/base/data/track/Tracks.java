package com.github.pfichtner.jrunalyser.base.data.track;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.DivideTrack;
import com.github.pfichtner.jrunalyser.base.data.GeoUtil;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenters;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Tracks {

	private Tracks() {
		super();
	}

	public static LinkedTrackPoint getStartPoint(Track track) {
		return checkNotEmpty(track.getTrackpoints()).get(0);
	}

	public static LinkedTrackPoint getEndPoint(Track track) {
		return Iterables.getLast(checkNotEmpty(track.getTrackpoints()));
	}

	private static <T> List<T> checkNotEmpty(List<T> wps) {
		checkArgument(!wps.isEmpty(), "Track has no waypoints");
		return wps;
	}

	/**
	 * Creates a new Track with all trackpoints (and segments) reversed. The
	 * statistics are recalculated since they could differ from the original's
	 * one
	 * 
	 * @return reversed Track
	 */
	public static Track reverseView(final Track track) {
		return new Track() {

			@Override
			public Statistics getStatistics() {
				return DefaultStatistics.ofTrack(this);
			}

			@Override
			public List<? extends LinkedTrackPoint> getTrackpoints() {
				return Lists.reverse(track.getTrackpoints());
			}

			@Override
			public List<Segment> getSegments() {
				return Lists.reverse(track.getSegments());
			}

			@Override
			public Metadata getMetadata() {
				return track.getMetadata();
			}

			@Override
			public Id getId() {
				return track.getId();
			}

			@Override
			public List<? extends WayPoint> getWaypoints() {
				return track.getWaypoints();
			}

		};
	}

	public static Track reverse(Track track) {
		Track view = reverseView(track);
		return new DefaultTrack(view.getId(), view.getMetadata(),
				track.getWaypoints(), view.getSegments(), view.getStatistics());
	}

	/**
	 * Returns <code>true</code> if startpos and endpos of the track are nearby.
	 * 
	 * @param track
	 *            the track to check
	 * @return <code>true</code> if startpos and endpos are nearby
	 */
	public static boolean isRoundtrip(Track track) {
		Distance maxDiff = DefaultDistance.of(75, DistanceUnit.METERS);
		Distance diffMeters = GeoUtil.calcDistance(getStartPoint(track),
				getEndPoint(track));
		return diffMeters.compareTo(maxDiff) <= 0;
	}

	/**
	 * Returns <code>true</code> if the track's away and return ways are
	 * (nearly) identical.
	 * 
	 * @param track
	 *            the track to check
	 * @return <code>true</code> if the track's away and return ways are
	 *         (nearly) identical
	 */
	public static boolean isAwayEqReturn(Track track) {
		Distance maxDiff = DefaultDistance.of(75, DistanceUnit.METERS);
		int parts = 20; // must be even!

		List<Segment> segments = Segmenters
				.getSegmenter(new DivideTrack(parts, Distance.class))
				.segment(track).getSegments();

		for (int i = 0; i < segments.size() / 2; i++) {
			WayPoint fw = Iterables.get(segments.get(i).getTrackpoints(), 0);
			WayPoint bw = Iterables.getLast(segments.get(
					segments.size() - 1 - i).getTrackpoints());
			Distance diffMeters = GeoUtil.calcDistance(fw, bw);
			if (diffMeters.compareTo(maxDiff) > 0) {
				return false;
			}
		}
		return true;
	}

}
