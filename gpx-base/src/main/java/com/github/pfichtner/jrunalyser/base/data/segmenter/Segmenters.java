package com.github.pfichtner.jrunalyser.base.data.segmenter;

import static com.google.common.base.Functions.compose;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.DivideTrack;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.floater.FloatingSegmenter;
import com.github.pfichtner.jrunalyser.base.data.segment.DefaultSegment;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.stat.Functions;
import com.github.pfichtner.jrunalyser.base.data.stat.StatCalculators;
import com.github.pfichtner.jrunalyser.base.data.stat.StatCalculators.StatCalculator;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class Segmenters {

	/**
	 * Segments the track into several parts (e.g. based on durations or
	 * distances).
	 * 
	 * @author Peter Fichtner
	 */
	private static abstract class AbtstractDivideTrackSegmenter implements
			Segmenter {

		private final int parts;

		public AbtstractDivideTrackSegmenter(int parts) {
			this.parts = parts;
		}

		/**
		 * Returns the amount of parts this segmenter segments.
		 * 
		 * @return amount of parts this segmenter segments
		 */
		protected int getParts() {
			return this.parts;
		}

		@Override
		public final Track segment(Track track) {
			Segmenter delegate = createDelegate(track);
			Track segmentedTrack = delegate.segment(track);
			Track result = segmentedTrack.getSegments().size() > getParts() ? fix(segmentedTrack)
					: segmentedTrack;
			assert result.getSegments().size() == getParts() : "Track was segmented into "
					+ result.getSegments().size()
					+ " parts "
					+ " but result should have been " + getParts() + " parts";
			return result;
		}

		private Track fix(Track track) {
			List<Segment> newSegments = Lists.newArrayList();
			if (getParts() > 2) {
				newSegments.addAll(track.getSegments().subList(0,
						getParts() - 1));
			}
			// merge last-but-one and last
			List<LinkedTrackPoint> wps = mergeLastTwo(track);
			newSegments.add(new DefaultSegment(wps, DefaultStatistics
					.ofWaypoints(wps)));

			return new DefaultTrack(track.getId(), track.getMetadata(),
					track.getWaypoints(), newSegments, track.getStatistics());
		}

		private List<LinkedTrackPoint> mergeLastTwo(Track track) {
			List<Segment> segments = track.getSegments();
			return new ImmutableList.Builder<LinkedTrackPoint>()
					.addAll(segments.get(getParts() - 2).getTrackpoints())
					.addAll(segments.get(getParts() - 1).getTrackpoints())
					.build();
		}

		protected abstract Segmenter createDelegate(Track track);

	}

	private static class DivideTrackSegmenterDuration extends
			AbtstractDivideTrackSegmenter {

		public DivideTrackSegmenterDuration(int parts) {
			super(parts);
		}

		@Override
		protected Segmenter createDelegate(Track track) {
			double seconds = track.getStatistics().getDuration()
					.getValue(TimeUnit.SECONDS)
					/ getParts();
			return getSegmenter(DefaultDuration.of(seconds, TimeUnit.SECONDS));
		}
	}

	private static class DivideTrackSegmenterDistance extends
			AbtstractDivideTrackSegmenter {

		public DivideTrackSegmenterDistance(int parts) {
			super(parts);
		}

		protected Segmenter createDelegate(Track track) {
			double meters = track.getStatistics().getDistance()
					.getValue(DistanceUnit.METERS)
					/ getParts();
			return getSegmenter(DefaultDistance.of(meters, DistanceUnit.METERS));
		};

	}

	private Segmenters() {
		super();
	}

	public static Segmenter duration(Duration duration) {
		Function<LinkedTrackPoint, Duration> dataFunc = compose(
				Functions.Links.duration(), Functions.LinkedWayPoints.link());
		return new MathObjectSegmenter<Duration>(duration, DefaultDuration.of(
				0, duration.getTimeUnit()), dataFunc);

	}

	public static Segmenter distance(Distance distance) {
		Function<LinkedTrackPoint, Distance> dataFunc = compose(
				Functions.Links.distance(), Functions.LinkedWayPoints.link());
		return new MathObjectSegmenter<Distance>(distance, DefaultDistance.of(
				0, distance.getDistanceUnit()), dataFunc);

	}

	public static Segmenter getSegmenter(SegmentationUnit unit) {
		if (unit == null) {
			return Segmenter.NULL_SEGMENTER;
		} else if (unit instanceof Duration) {
			return duration((Duration) unit);
		} else if (unit instanceof Distance) {
			return distance((Distance) unit);
		} else if (unit instanceof DivideTrack
				&& ((DivideTrack) unit).getBasedOn() == Duration.class) {
			return new DivideTrackSegmenterDuration(
					((DivideTrack) unit).getParts());
		} else if (unit instanceof DivideTrack
				&& ((DivideTrack) unit).getBasedOn() == Distance.class) {
			return new DivideTrackSegmenterDistance(
					((DivideTrack) unit).getParts());
		}
		throw new IllegalStateException("Cannot handle " + unit);
	}

	// ---------------------------------------------------------------------------

	public static Segmenter floatingDuration(Duration duration) {
		StatCalculator<Duration> distCalc = StatCalculators
				.duration(TimeUnit.MINUTES);
		StatCalculator<Speed> valueCalc = StatCalculators.avgSpeed(
				DistanceUnit.KILOMETERS, TimeUnit.HOURS);
		return new FloatingSegmenter<Duration, Speed>(duration, distCalc,
				valueCalc);
	}

	public static Segmenter floatingDistance(Distance distance) {
		StatCalculator<Distance> distCalc = StatCalculators
				.distance(DistanceUnit.METERS);
		StatCalculator<Speed> valueCalc = StatCalculators.avgSpeed(
				DistanceUnit.KILOMETERS, TimeUnit.HOURS);
		return new FloatingSegmenter<Distance, Speed>(distance, distCalc,
				valueCalc);
	}

	public static Segmenter getFloatingSegmenter(SegmentationUnit unit) {
		if (unit == null) {
			return Segmenter.NULL_SEGMENTER;
		} else if (unit instanceof Duration) {
			return floatingDuration((Duration) unit);
		} else if (unit instanceof Distance) {
			return floatingDistance((Distance) unit);
		}
		throw new IllegalStateException("Cannot handle " + unit);
	}

}
