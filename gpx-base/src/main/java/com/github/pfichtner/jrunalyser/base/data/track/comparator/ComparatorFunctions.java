package com.github.pfichtner.jrunalyser.base.data.track.comparator;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.Coordinate;
import com.github.pfichtner.jrunalyser.base.data.DefaultCoordinate;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Distances;
import com.github.pfichtner.jrunalyser.base.data.GeoUtil;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenter;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.Tracks;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public final class ComparatorFunctions {

	private ComparatorFunctions() {
		super();
	}

	public static Function<Track, Number> trackWidth(
			final DistanceUnit distanceUnit) {
		return new Function<Track, Number>() {
			@Override
			public Number apply(Track track) {
				double top = track.getMetadata().getMinLatitude();
				double left = track.getMetadata().getMinLongitude();
				double right = track.getMetadata().getMaxLongitude();

				Coordinate startPoint = new DefaultCoordinate(top, left);
				Coordinate endPoint = new DefaultCoordinate(top, right);
				return Double.valueOf(Distances.abs(
						GeoUtil.calcDistance(startPoint, endPoint)).getValue(
						distanceUnit));

			}

			@Override
			public String toString() {
				return "trackWidth";
			}
		};
	}

	public static Function<Track, Number> trackHeight(
			final DistanceUnit distanceUnit) {
		return new Function<Track, Number>() {
			@Override
			public Number apply(Track track) {
				double left = track.getMetadata().getMinLongitude();
				double top = track.getMetadata().getMinLatitude();
				double bottom = track.getMetadata().getMaxLatitude();

				Coordinate startPoint = new DefaultCoordinate(top, left);
				Coordinate endPoint = new DefaultCoordinate(bottom, left);
				return Double.valueOf(Distances.abs(
						GeoUtil.calcDistance(startPoint, endPoint)).getValue(
						distanceUnit));
			}

			@Override
			public String toString() {
				return "trackHeight";
			}

		};
	}

	public static Function<Track, Number> trackMinEle(
			final DistanceUnit distanceUnit) {
		return new Function<Track, Number>() {
			@Override
			public Number apply(Track track) {
				return track.getStatistics().getMinElevation().getElevation();
			}

			@Override
			public String toString() {
				return "trackMinEle";
			}

		};
	}

	public static Function<Track, Number> trackMaxEle(
			final DistanceUnit distanceUnit) {
		return new Function<Track, Number>() {
			@Override
			public Number apply(Track track) {
				return track.getStatistics().getMaxElevation().getElevation();
			}

			@Override
			public String toString() {
				return "trackMaxEle";
			}

		};
	}

	public static Function<Track, Number> trackLength(
			final DistanceUnit distanceUnit) {
		return new Function<Track, Number>() {
			@Override
			public Number apply(Track track) {
				return Double.valueOf(track.getStatistics().getDistance()
						.getValue(distanceUnit));
			}

			@Override
			public String toString() {
				return "trackLength";
			}

		};
	}

	public static Function<Track, Coordinate> trackLeftBottom() {
		return new Function<Track, Coordinate>() {
			@Override
			public Coordinate apply(Track track) {
				return new DefaultCoordinate(track.getMetadata()
						.getMinLatitude(), track.getMetadata()
						.getMinLongitude());
			}

			@Override
			public String toString() {
				return "trackTopLeft";
			}

		};
	}

	public static Function<Track, WayPoint> startPos() {
		return new Function<Track, WayPoint>() {
			@Override
			public WayPoint apply(Track track) {
				return Tracks.getStartPoint(track);
			}

			@Override
			public String toString() {
				return "startPos";
			}

		};
	}

	public static Function<Track, WayPoint> endPos() {
		return new Function<Track, WayPoint>() {
			@Override
			public WayPoint apply(Track track) {
				return Tracks.getEndPoint(track);
			}

			@Override
			public String toString() {
				return "endPos";
			}

		};
	}

	/**
	 * Creates a Function that will return an Iterable of WayPoints representing
	 * the start points of segments.
	 * 
	 * @param segmenter
	 *            the Segmenter to use
	 * @return Function that will return an Iterable of WayPoints representing
	 *         the start points of segments
	 */
	public static Function<Track, Iterable<? extends WayPoint>> segmentStartPoints(
			final Segmenter segmenter) {
		return new Function<Track, Iterable<? extends WayPoint>>() {
			@Override
			public Iterable<WayPoint> apply(Track track) {
				Function<Segment, WayPoint> getFirst = new Function<Segment, WayPoint>() {
					@Override
					public WayPoint apply(Segment segment) {
						List<? extends LinkedTrackPoint> waypoints = segment
								.getTrackpoints();
						// there can't be a segment without a waypoint
						return checkNotNull(
								Iterables.getFirst(waypoints, null),
								"No waypoint found in %s", waypoints);
					}
				};
				return Iterables.transform(segmenter.segment(track)
						.getSegments(), getFirst);
			}

			@Override
			public String toString() {
				return "segmentStartPoints";
			}
		};
	}

}
