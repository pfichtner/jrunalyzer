package com.github.pfichtner.jrunalyser.base.data.track.comparator;

import static com.github.pfichtner.jrunalyser.base.data.track.comparator.ComparatorFunctions.endPos;
import static com.github.pfichtner.jrunalyser.base.data.track.comparator.ComparatorFunctions.segmentStartPoints;
import static com.github.pfichtner.jrunalyser.base.data.track.comparator.ComparatorFunctions.startPos;
import static com.github.pfichtner.jrunalyser.base.data.track.comparator.ComparatorFunctions.trackHeight;
import static com.github.pfichtner.jrunalyser.base.data.track.comparator.ComparatorFunctions.trackLeftBottom;
import static com.github.pfichtner.jrunalyser.base.data.track.comparator.ComparatorFunctions.trackLength;
import static com.github.pfichtner.jrunalyser.base.data.track.comparator.ComparatorFunctions.trackMaxEle;
import static com.github.pfichtner.jrunalyser.base.data.track.comparator.ComparatorFunctions.trackMinEle;
import static com.github.pfichtner.jrunalyser.base.data.track.comparator.ComparatorFunctions.trackWidth;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.github.pfichtner.jrunalyser.base.comparator.RelDiffNumberComparator;
import com.github.pfichtner.jrunalyser.base.data.Coordinate;
import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.DefaultWayPoint;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Distances;
import com.github.pfichtner.jrunalyser.base.data.GeoUtil;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.GeoUtil.BearingInfo;
import com.github.pfichtner.jrunalyser.base.data.GeoUtil.DefaultBearingInfo;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenter;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenters;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.Tracks;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.CombinedIterator.Pair;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

/**
 * Utility class holding references to {@link Comparator}s for {@link Track}s.
 * 
 * @author Peter Fichtner
 */
public final class TrackComparators {

	public interface FBC<V> {

		V applyA(Track a);

		V applyB(Track b);

	}

	/**
	 * ChainedComparator calls {@link Comparator#compare(Object, Object)} on
	 * each comparator of the chain until the compare result is
	 * <code>!= 0</code>, this result of this last {@link Comparator} is
	 * returned or <code>0</code> if all compare results were <code>0</code>,
	 * 
	 * @author Peter Fichtner
	 * 
	 * @param <T>
	 *            type this Comparator compares
	 */
	public static class ChainedComparator<T> implements Comparator<T> {

		private final List<Comparator<T>> comparators;

		public ChainedComparator(Comparator<T> comparator) {
			this.comparators = ImmutableList.of(comparator);
		}

		public ChainedComparator(Iterable<Comparator<T>> comparators) {
			this.comparators = ImmutableList.copyOf(comparators);
		}

		public ChainedComparator<T> add(Comparator<T> comparator) {
			return new ChainedComparator<T>(base().add(comparator).build());
		}

		public ChainedComparator<T> addAll(Iterable<Comparator<T>> comparators) {
			return new ChainedComparator<T>(base().addAll(comparators).build());
		}

		private Builder<Comparator<T>> base() {
			return ImmutableList.<Comparator<T>> builder().addAll(
					this.comparators);
		}

		/**
		 * Returns the (immutable) List of underlying {@link Comparator}s.
		 * 
		 * @return immutable List of underlying {@link Comparator}s
		 */
		public List<Comparator<T>> getComparators() {
			return this.comparators;
		}

		@Override
		public int compare(T t1, T t2) {
			ComparisonChain chain = ComparisonChain.start();
			for (Comparator<T> comparator : this.comparators) {
				chain = chain.compare(t1, t2, comparator);
			}
			return chain.result();
		}

	}

	private static final Function<Track, Track> revTrack = new Function<Track, Track>() {
		@Override
		public Track apply(Track track) {
			return Tracks.reverse(track);
		}
	};

	private static final BearingInfo ZERO_BEARING = new DefaultBearingInfo(
			DefaultDistance.of(0, DistanceUnit.METERS), 0);

	private TrackComparators() {
		super();
	}

	/**
	 * A Comparator that calls the <code>Function</code> to get a <code>T</code>
	 * from both tracks (e.g. length) and than delegates to the comparator by
	 * passing those two <code>T</code>s.
	 * 
	 * @author Peter Fichtner
	 * 
	 * @param <T>
	 *            type that is compared
	 */
	public static class FunctionBasedComparator<T> implements
			Comparator<Track>, FBC<T> {

		private final Function<Track, T> function;
		private final Comparator<T> comparator;

		public FunctionBasedComparator(Function<Track, T> function,
				Comparator<T> comparator) {
			this.function = function;
			this.comparator = comparator;
		}

		@Override
		public int compare(Track t1, Track t2) {
			return this.comparator.compare(applyA(t1), applyB(t2));
		}

		@Override
		public T applyA(Track a) {
			return this.function.apply(a);
		}

		@Override
		public T applyB(Track b) {
			return this.function.apply(b);
		}

		@Override
		public String toString() {
			return "FunctionBasedComparator [function=" + this.function
					+ ", comparator=" + this.comparator + "]";
		}

	}

	private static class WaypointDistanceComparator implements
			Comparator<Track>, FBC<Coordinate> {

		private final Function<Track, ? extends Coordinate> function;
		private final Distance maxDiff;

		public WaypointDistanceComparator(
				Function<Track, ? extends Coordinate> function, Distance maxDiff) {
			this.function = function;
			this.maxDiff = Distances.abs(maxDiff);
		}

		@Override
		public int compare(Track t1, Track t2) {
			Distance diff = Distances.abs(GeoUtil.calcDistance(applyA(t1),
					applyB(t2)));
			return diff.compareTo(this.maxDiff) <= 0 ? 0 : diff
					.compareTo(this.maxDiff);
		}

		@Override
		public Coordinate applyA(Track a) {
			return this.function.apply(a);
		}

		@Override
		public Coordinate applyB(Track b) {
			return this.function.apply(b);
		}

		@Override
		public String toString() {
			return "WaypointDistanceComparator [function=" + this.function
					+ ", maxDiff=" + this.maxDiff + "]";
		}

	}

	private static class MaxAbsComparator implements Comparator<Track>,
			FBC<Number> {

		private final Function<Track, Number> function;
		private final BigDecimal maxDiff;

		public MaxAbsComparator(Function<Track, Number> function,
				BigDecimal maxDiff) {
			this.function = function;
			this.maxDiff = maxDiff;
		}

		@Override
		public int compare(Track t1, Track t2) {
			BigDecimal diff = new BigDecimal(String.valueOf(applyA(t1)))
					.subtract(new BigDecimal(String.valueOf(applyB(t2))));
			return diff.abs().compareTo(this.maxDiff) <= 0 ? 0
					: diff.signum() > 0 ? 1 : -1;
		}

		@Override
		public Number applyA(Track a) {
			return this.function.apply(a);
		}

		@Override
		public Number applyB(Track b) {
			return this.function.apply(b);
		}

		@Override
		public String toString() {
			return "MaxAbsComparator [function=" + this.function + ", maxDiff="
					+ this.maxDiff + "]";
		}

	}

	/**
	 * Returns an Iterator that returns the differences of each waypoint pair of
	 * <code>it1</code> and <code>it2</code>.
	 * 
	 * @param it1
	 *            waypoints 1
	 * @param it2
	 *            waypoints 2
	 * @return Iterator returning the differences of each waypoint pair
	 */
	private static <T extends WayPoint> CombinedIterator<T, Distance> wpDistanceDiffIterator(
			Iterator<? extends T> it1, Iterator<? extends T> it2) {
		return new CombinedIterator<T, Distance>(it1, it2,
				new Function<Pair<T>, Distance>() {
					@Override
					public Distance apply(Pair<T> pair) {
						// TODO delegate to MaxAbsComparator
						T wp1 = pair.getValue1();
						T wp2 = pair.getValue2();
						return Distances.abs(GeoUtil.calcDistance(wp1, wp2));
					}

					@Override
					public String toString() {
						return "WpDistanceDiffIterator";
					}
				});

	}

	/**
	 * A Comparator that returns <code>true</code> if <b>all</b> waypoints
	 * returned by the function(s) have a maxDiff of <code>maxDiff</code>.
	 * 
	 * @author Peter Fichtner
	 */
	public static class MultiWaypointDistanceComparator implements
			Comparator<Track>, FBC<Iterable<? extends WayPoint>> {

		private final Function<Track, ? extends Iterable<? extends WayPoint>> f1;
		private final Function<Track, ? extends Iterable<? extends WayPoint>> f2;
		private final Distance maxDiff;

		public MultiWaypointDistanceComparator(
				Function<Track, Iterable<WayPoint>> function, Distance maxDiff) {
			this(function, function, maxDiff);
		}

		public MultiWaypointDistanceComparator(
				Function<Track, ? extends Iterable<? extends WayPoint>> f1,
				Function<Track, ? extends Iterable<? extends WayPoint>> f2,
				Distance maxDiff) {
			this.f1 = f1;
			this.f2 = f2;
			this.maxDiff = Distances.abs(maxDiff);
		}

		@Override
		public Iterable<? extends WayPoint> applyA(Track a) {
			return this.f1.apply(a);
		}

		@Override
		public Iterable<? extends WayPoint> applyB(Track b) {
			return this.f2.apply(b);
		}

		@Override
		public int compare(Track t1, Track t2) {
			CombinedIterator<? extends WayPoint, Distance> wpDistanceDiffIterator = getDiffs(
					t1, t2);
			for (CombinedIterator<? extends WayPoint, Distance> iterator = wpDistanceDiffIterator; iterator
					.hasNext();) {
				Distance distance = iterator.next();
				int cmp = Distances.abs(distance).compareTo(this.maxDiff);
				if (cmp > 0) {
					return cmp;
				}
			}
			// do not check wpDistanceDiffIterator#isTotallyCollected() since a
			// Track with 4,301km could contain 43 elements and the other with
			// just 4,299 "only" 42
			return 0;
		}

		/**
		 * Returns an Iterator holding the differences of the waypoints of the
		 * two tracks.
		 * 
		 * @param t1
		 *            Track 1
		 * @param t2
		 *            Track 2
		 * @return Iterator holding the differences of the waypoints
		 * @see TrackComparators#wpDistanceDiffIterator(Track, Track)
		 */
		public CombinedIterator<? extends WayPoint, Distance> getDiffs(
				Track t1, Track t2) {
			return wpDistanceDiffIterator(applyA(t1).iterator(), applyB(t2)
					.iterator());
		}

	}

	/**
	 * Creates a new Comparator for Tracks that will return <code>0</code> on
	 * the following conditions:
	 * <ul>
	 * <li>The difference of the length of the tracks must be not more than 8%</li>
	 * <li>The difference of the width of the tracks must be not more than 8%</li>
	 * <li>The difference of the height of the tracks must be not more than 8%</li>
	 * <li>The difference of the tracks' Southwest corner must be not more than
	 * 80 meters</li>
	 * <li>The difference of the tracks' startpoints must be not more than 80
	 * meters</li>
	 * <li>The difference of the tracks' endpoints must be not more than 80
	 * meters</li>
	 * <li>The difference of the tracks' min. elevation must be not more than 30
	 * meters</li>
	 * <li>The difference of the tracks' max. elevation must be not more than 30
	 * meters</li>
	 * </ul>
	 */
	public static ChainedComparator<Track> baseAttributes = builder()
			.addRel(trackLength(DistanceUnit.METERS), 8)
			.addRel(trackWidth(DistanceUnit.METERS), 8)
			.addRel(trackHeight(DistanceUnit.METERS), 8)
			.addAbs(trackLeftBottom(),
					DefaultDistance.of(80, DistanceUnit.METERS))
			.addAbs(startPos(), DefaultDistance.of(100, DistanceUnit.METERS))
			.addAbs(endPos(), DefaultDistance.of(100, DistanceUnit.METERS))
			.addAbsX(trackMinEle(DistanceUnit.METERS), BigDecimal.valueOf(30))
			.addAbsX(trackMaxEle(DistanceUnit.METERS), BigDecimal.valueOf(30))
			.build();

	private static Comparator<Track> rel(Function<Track, Number> function,
			int maxDiff) {
		return new FunctionBasedComparator<Number>(function,
				new RelDiffNumberComparator(maxDiff));
	}

	private static WaypointDistanceComparator absSingle(
			final Function<Track, ? extends Coordinate> function,
			final Distance maxDiff) {
		return new WaypointDistanceComparator(function, maxDiff);
	}

	private static MaxAbsComparator absSingleX(
			final Function<Track, Number> function, final BigDecimal maxDiff) {
		return new MaxAbsComparator(function, maxDiff);
	}

	private static MultiWaypointDistanceComparator absMulti(
			Function<Track, Iterable<WayPoint>> function, Distance maxDiff) {
		return absMulti(function, function, maxDiff);
	}

	private static MultiWaypointDistanceComparator absMulti(
			Function<Track, ? extends Iterable<? extends WayPoint>> f1,
			Function<Track, ? extends Iterable<? extends WayPoint>> f2,
			Distance maxDiff) {
		return new MultiWaypointDistanceComparator(f1, f2, maxDiff);
	}

	private static MultiWaypointDistanceComparator abs(
			Function<Track, ? extends Iterable<? extends WayPoint>> f1,
			Function<Track, ? extends Iterable<? extends WayPoint>> f2,
			Distance maxDiff) {
		return new MultiWaypointDistanceComparator(f1, f2, maxDiff);
	}

	public static MultiWaypointDistanceComparator segmentStartPointsEqual(
			Track t1, Track t2, Distance maxDiff) {
		return segmentStartPointsEqual(t1, t2, maxDiff, 12);
	}

	public static MultiWaypointDistanceComparator segmentStartPointsEqual(
			Track t1, Track t2, Distance distance, int segments) {
		Function<Track, Iterable<? extends WayPoint>> f1 = segmentStartPoints(createSegmenter(
				t1, t2, segments));
		Function<Track, ? extends Iterable<? extends WayPoint>> f2 = bearingDecorater(
				t1, t2, f1);
		return absMulti(f1, f2, distance);
	}

	// same than segmentStartPointsEqual but returns true if the two track's
	// segments are reversed order
	private static Comparator<Track> segmentStartPointsEqualReverseOrder(
			Track t1, Track t2, Distance distance, int segments) {
		Segmenter segmenter = createSegmenter(t1, t2, segments);
		Function<Track, Iterable<? extends WayPoint>> f1 = segmentStartPoints(segmenter);
		Function<Track, ? extends Iterable<? extends WayPoint>> f2 = bearingDecorater(
				t1, t2, f1);
		return abs(f1, Functions.compose(f2, revTrack), distance);
	}

	// ----------------------------------------------------------------------------------

	/**
	 * Creates a Decorator that returns each Waypoint projected using the
	 * {@link BearingInfo} that is created by comparing both start positions.
	 * 
	 * @param bearingInfo
	 *            bearing data
	 * @param in
	 *            the Function to decorate
	 * @return decorator Function
	 */
	public static Function<Track, ? extends Iterable<? extends WayPoint>> bearingDecorater(
			Track track1, Track track2,
			final Function<Track, ? extends Iterable<? extends WayPoint>> in) {

		final BearingInfo bearingInfo = GeoUtil.bearingInfo(
				Tracks.getStartPoint(track1), Tracks.getStartPoint(track2));

		return ZERO_BEARING.equals(bearingInfo) ? in
				: new Function<Track, Iterable<? extends WayPoint>>() {
					@Override
					public Iterable<WayPoint> apply(Track track) {
						return Iterables.transform(in.apply(track),
								bearingFunction(bearingInfo));
					}

				};
	}

	/**
	 * Creates a Decorator that returns the Waypoints starting by 0.0/0.0.
	 * 
	 * @param bearingInfo
	 *            bearing data
	 * @param in
	 *            the Function to decorate
	 * @return decorator Function
	 */
	public static Function<Track, ? extends Iterable<? extends WayPoint>> bearingZeroZeroDecorater(
			final Function<Track, ? extends Iterable<? extends WayPoint>> in) {
		return new Function<Track, Iterable<? extends WayPoint>>() {
			@Override
			public Iterable<WayPoint> apply(Track track) {
				final BearingInfo bearingInfo = GeoUtil.bearingInfo(
						Tracks.getStartPoint(track), Coordinate.ZERO_ZERO);
				return Iterables.transform(in.apply(track),
						bearingFunction(bearingInfo));
			}

		};
	}

	private static Function<WayPoint, WayPoint> bearingFunction(
			final BearingInfo bearingInfo) {
		return new Function<WayPoint, WayPoint>() {
			@Override
			public WayPoint apply(WayPoint wayPoint) {
				Coordinate projected = GeoUtil.project(bearingInfo, wayPoint);
				return new DefaultWayPoint(projected.getLatitude(),
						projected.getLongitude(), wayPoint.getElevation(),
						wayPoint.getTime());
			}

			@Override
			public String toString() {
				return "BearingDecorator [bearingInfo=" + bearingInfo + "]";

			}
		};
	}

	// ----------------------------------------------------------------------------------

	/**
	 * Creates a Segmenter that segments a track based on the length of the two
	 * tracks and the amount of checkpoints. If there are two tracks of each
	 * 2000m length and an amount of 4 checkpoints the segments will segment
	 * into 500m segments.
	 * 
	 * @param t1
	 *            Track #1
	 * @param t2
	 *            Track #2
	 * @param checkpoints
	 *            amount of checkpoints
	 * @return Segmenter that segments a track based on the length of the two
	 *         tracks and the amount of checkpoints
	 */
	private static Segmenter createSegmenter(Track t1, Track t2, int checkpoints) {
		return Segmenters.distance(DefaultDistance.of(getAvgLength(t1, t2)
				/ checkpoints, DistanceUnit.METERS));
	}

	private static double getAvgLength(Track t1, Track t2) {
		return t1.getStatistics().getDistance()
				.add(t2.getStatistics().getDistance())
				.getValue(DistanceUnit.METERS) / 2;
	}

	/**
	 * Creates a new Comparator for Tracks that will return <code>0</code> on
	 * the following conditions:
	 * <ul>
	 * <li>The difference of the length of the tracks must be not more than 8%</li>
	 * <li>The difference of the width of the tracks must be not more than 8%</li>
	 * <li>The difference of the height of the tracks must be not more than 8%</li>
	 * <li>The difference of the tracks' Northwest corner must be not more than
	 * 80 meters</li>
	 * <li>The difference of the tracks' startpoints must be not more than 80
	 * meters</li>
	 * <li>The difference of the tracks' endpoints must be not more than 80
	 * meters</li>
	 * <li>The difference of the tracks' min. elevation must be not more than 8
	 * percent</li>
	 * <li>The difference of the tracks' max. elevation must be not more than 8
	 * percent</li>
	 * <li>The difference of the tracks' 12 checkpoints all n meters must be not
	 * more than 100 meters</li>
	 * </ul>
	 */
	public static Comparator<Track> byAttributes = new Comparator<Track>() {
		@Override
		public int compare(Track t1, Track t2) {
			Comparator<Track> segmentStartPointsEqual = segmentStartPointsEqual(
					t1, t2, DefaultDistance.of(150, DistanceUnit.METERS));

			return ComparisonChain.start().compare(t1, t2, baseAttributes)
					.compare(t1, t2, segmentStartPointsEqual).result();
		}
	};

	public static Comparator<Track> byDescription = new Comparator<Track>() {
		@Override
		public int compare(Track t1, Track t2) {
			CharMatcher javaLetterOrDigit = CharMatcher.JAVA_LETTER_OR_DIGIT;
			String d1 = t1.getMetadata().getDescription();
			String d2 = t2.getMetadata().getDescription();
			return javaLetterOrDigit.retainFrom(d1).toLowerCase()
					.compareTo(javaLetterOrDigit.retainFrom(d2).toLowerCase());
		}
	};

	public static class TrackComparatorBuilder {

		private List<Comparator<Track>> comparators = Lists.newArrayList();

		public TrackComparatorBuilder addRel(Function<Track, Number> function,
				int maxDiff) {
			this.comparators.add(rel(function, maxDiff));
			return this;
		}

		public TrackComparatorBuilder addAbs(
				Function<Track, ? extends Coordinate> function,
				Distance distance) {
			this.comparators.add(absSingle(function, distance));
			return this;
		}

		public TrackComparatorBuilder addAbsX(Function<Track, Number> function,
				BigDecimal maxDiff) {
			this.comparators.add(absSingleX(function, maxDiff));
			return this;
		}

		public ChainedComparator<Track> build() {
			return new ChainedComparator<Track>(this.comparators);
		}
	}

	public static TrackComparatorBuilder builder() {
		return new TrackComparatorBuilder();
	}

}
