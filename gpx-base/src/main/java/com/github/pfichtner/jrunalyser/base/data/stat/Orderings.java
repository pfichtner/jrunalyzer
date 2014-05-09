package com.github.pfichtner.jrunalyser.base.data.stat;

import static com.google.common.base.Functions.compose;

import java.util.Comparator;

import javax.annotation.Nullable;

import com.github.pfichtner.jrunalyser.base.Delegates;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.track.StatisticsProvider;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

public final class Orderings {

	private Orderings() {
		super();
	}

	// -----------------------------------------------------------------------------------------

	public static final Ordering<WayPoint> elevationOrdering = Ordering
			.natural().nullsLast().onResultOf(Functions.WayPoints.elevation());

	// -----------------------------------------------------------------------------------------

	public static final Ordering<WayPoint> latitudeOrdering = Ordering
			.natural().onResultOf(Functions.WayPoints.latitude());
	public static final Ordering<WayPoint> longitudeOrdering = Ordering
			.natural().onResultOf(Functions.WayPoints.longitude());
	// -----------------------------------------------------------------------------------------

	public static Function<LinkedTrackPoint, Speed> speedOrderingFunc = Functions.LinkedWayPoints
			.speed();

	public static final Ordering<LinkedTrackPoint> speedOrdering = Ordering
			.natural().onResultOf(speedOrderingFunc);
	// -----------------------------------------------------------------------------------------

	public static final Function<StatisticsProvider, Distance> distanceFunc = compose(
			Functions.Statisticss.distance,
			Functions.StatisticsProviders.statistics);

	public static final Ordering<StatisticsProvider> distance = Ordering
			.natural().onResultOf(distanceFunc);

	public static final Function<StatisticsProvider, Duration> durationFunc = compose(
			Functions.Statisticss.duration,
			Functions.StatisticsProviders.statistics);

	public static final Ordering<StatisticsProvider> duration = Ordering
			.natural().onResultOf(durationFunc);

	// -----------------------------------------------------------------------------------------

	public static final Ordering<Track> time = Ordering.natural().onResultOf(
			compose(Functions.WayPoints.time(),
					compose(Functions.Collections.<LinkedTrackPoint> get0(),
							Functions.Tracks.trackpoints)));

	// -----------------------------------------------------------------------------------------

	public static final Function<Track, Double> maxLatitudeFunc = compose(
			Functions.Metadatas.maxLatitude, Functions.Tracks.metadata);

	public static final Function<Track, Double> minLatitudeFunc = compose(
			Functions.Metadatas.minLatitude, Functions.Tracks.metadata);

	public static final Function<Track, Double> maxLongitudeFunc = compose(
			Functions.Metadatas.maxLongitude, Functions.Tracks.metadata);

	public static final Function<Track, Double> minLongitudeFunc = compose(
			Functions.Metadatas.minLongitude, Functions.Tracks.metadata);

	public static final Ordering<Track> maxLatitude = Ordering.natural()
			.onResultOf(maxLatitudeFunc);

	public static final Ordering<Track> minLatitude = Ordering.natural()
			.onResultOf(minLatitudeFunc);

	public static final Ordering<Track> maxLongitude = Ordering.natural()
			.onResultOf(maxLongitudeFunc);

	public static final Ordering<Track> minLongitude = Ordering.natural()
			.onResultOf(minLongitudeFunc);

	// -----------------------------------------------------------------------------------------

	public static <T> Ordering<T> classTypeOrdering(
			final Iterable<Class<? extends T>> orderByClassType) {
		return classTypeOrdering(orderByClassType,
				Integer.valueOf(Integer.MIN_VALUE));
	}

	public static <T> Ordering<T> classTypeOrdering(
			final Iterable<Class<? extends T>> orderByClassType,
			final Integer useForNull) {
		return Ordering.from(new Comparator<T>() {
			@Override
			public int compare(T t1, T t2) {
				return idx(t1).or(useForNull).compareTo(idx(t2).or(useForNull));
			}

			private Optional<Integer> idx(T t) {
				int idx = Iterables.indexOf(
						orderByClassType,
						Predicates.assignableFrom(Delegates.getRoot(t,
								Object.class).getClass()));
				return idx < 0 ? Optional.<Integer> absent() : Optional
						.of(Integer.valueOf(idx));
			}
		});
	}

	// -----------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------

	private static final Function<Track, Statistics> getHlSegment = compose(
			com.github.pfichtner.jrunalyser.base.data.stat.Functions.StatisticsProviders.statistics,
			new Function<Track, Segment>() {
				@Override
				@Nullable
				public Segment apply(Track segmentedTrack) {
					return FluentIterable
							.from(segmentedTrack.getSegments())
							.filter(com.github.pfichtner.jrunalyser.base.data.stat.Predicates.Segments.isHighligted)
							.first().orNull();
				}
			});

	private static final Function<Statistics, Comparable<?>> getAvgSpeed = new Function<Statistics, Comparable<?>>() {
		@Override
		public Comparable<?> apply(Statistics statistics) {
			return statistics == null ? null : statistics.getAvgSpeed();
		}
	};

	/**
	 * An Ordering based on the average speed of the first highlighted segment
	 * of the track. If the Track is not segmented (or there is no highlighted
	 * segment) <code>null</code> is returned.
	 */
	public static final Ordering<Track> highlightedSpeedOrdering = Ordering
			.natural().reverse().nullsLast()
			.onResultOf(compose(getAvgSpeed, getHlSegment));

	// -----------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------

}
