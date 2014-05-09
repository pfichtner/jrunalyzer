package com.github.pfichtner.jrunalyser.base.data.stat;

import static com.github.pfichtner.jrunalyser.base.data.stat.Orderings.elevationOrdering;
import static com.github.pfichtner.jrunalyser.base.data.stat.Orderings.speedOrdering;
import static com.github.pfichtner.jrunalyser.base.data.stat.Predicates.hasValidSpeed;
import static com.github.pfichtner.jrunalyser.base.data.stat.Predicates.LinkedWayPoints.hasLink;
import static com.github.pfichtner.jrunalyser.base.data.stat.Predicates.LinkedWayPoints.hasNegativeElevationdiff;
import static com.github.pfichtner.jrunalyser.base.data.stat.Predicates.LinkedWayPoints.hasPositiveElevationdiff;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.DefaultSpeed;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.util.MovingAverageIterator;
import com.github.pfichtner.jrunalyser.base.util.Primitives;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

public class StatCalculators {

	public interface StatCalculator<T> {
		T calculate(Iterable<? extends LinkedTrackPoint> wayPoints);
	}

	// -----------------------------------------------------------------------------------

	public static StatCalculator<? extends LinkedTrackPoint> minEle() {
		return new StatCalculator<LinkedTrackPoint>() {
			@Override
			public LinkedTrackPoint calculate(
					Iterable<? extends LinkedTrackPoint> wayPoints) {
				Iterator<? extends LinkedTrackPoint> it = waypointsWithEle(
						wayPoints).iterator();
				return it.hasNext() ? elevationOrdering.min(it) : null;
			}
		};
	}

	public static StatCalculator<? extends LinkedTrackPoint> maxEle() {
		return new StatCalculator<LinkedTrackPoint>() {
			@Override
			public LinkedTrackPoint calculate(
					Iterable<? extends LinkedTrackPoint> wayPoints) {
				Iterator<? extends LinkedTrackPoint> it = waypointsWithEle(
						wayPoints).iterator();
				return it.hasNext() ? elevationOrdering.max(it) : null;
			}

		};
	}

	private static Iterable<? extends LinkedTrackPoint> waypointsWithEle(
			Iterable<? extends LinkedTrackPoint> wayPoints) {
		return filter(wayPoints, Predicates.WayPoints.hasElevation());
	}

	// -----------------------------------------------------------------------------------

	public static StatCalculator<Integer> ascent() {
		return new StatCalculator<Integer>() {
			@Override
			public Integer calculate(
					Iterable<? extends LinkedTrackPoint> wayPoints) {
				return Integer.valueOf(sumElevations(wayPoints,
						hasPositiveElevationdiff()));
			}

		};
	}

	public static StatCalculator<Integer> descent() {
		return new StatCalculator<Integer>() {
			@Override
			public Integer calculate(
					Iterable<? extends LinkedTrackPoint> wayPoints) {
				return Integer.valueOf(0 - sumElevations(wayPoints,
						hasNegativeElevationdiff()));
			}
		};
	}

	private static int sumElevations(
			Iterable<? extends LinkedTrackPoint> wayPoints,
			Predicate<Number> predicate) {
		List<Double> values = Lists.newArrayList(new MovingAverageIterator(
				transform(wayPoints, Functions.WayPoints.elevation())
						.iterator(), 3));
		return (int) Primitives.sum(Doubles.toArray(values), predicate);
	}

	// -----------------------------------------------------------------------------------

	public static StatCalculator<? extends LinkedTrackPoint> maxSpeed() {
		return new StatCalculator<LinkedTrackPoint>() {

			@Override
			public LinkedTrackPoint calculate(
					Iterable<? extends LinkedTrackPoint> wayPoints) {
				Iterable<? extends LinkedTrackPoint> validSpeedValues = filter(
						wayPoints, hasValidSpeed);
				return Iterables.isEmpty(validSpeedValues) ? null
						: speedOrdering.max(validSpeedValues);
			}
		};

	}

	public static StatCalculator<? extends LinkedTrackPoint> minSpeed() {
		return new StatCalculator<LinkedTrackPoint>() {
			@Override
			public LinkedTrackPoint calculate(
					Iterable<? extends LinkedTrackPoint> wayPoints) {
				Iterable<? extends LinkedTrackPoint> validSpeedValues = filter(
						wayPoints, hasValidSpeed);
				return Iterables.isEmpty(validSpeedValues) ? null
						: speedOrdering.min(validSpeedValues);
			}
		};
	}

	public static StatCalculator<Speed> avgSpeed(
			final DistanceUnit distanceUnit, final TimeUnit timeUnit) {
		return new StatCalculator<Speed>() {

			@Override
			public Speed calculate(Iterable<? extends LinkedTrackPoint> wayPoints) {
				return new DefaultSpeed(distance(distanceUnit).calculate(
						wayPoints), duration(timeUnit).calculate(wayPoints))
						.convert(distanceUnit, timeUnit);
			}
		};
	}

	// -----------------------------------------------------------------------------------

	public static StatCalculator<Distance> distance(
			final DistanceUnit distanceUnit) {
		return new StatCalculator<Distance>() {
			@Override
			public Distance calculate(
					Iterable<? extends LinkedTrackPoint> wayPoints) {
				double[] distances = Doubles.toArray(Lists
						.newArrayList(transform(filter(wayPoints, hasLink()),
								createDistanceFunc(distanceUnit))));
				return DefaultDistance.of(Primitives.sum(distances),
						distanceUnit);
			}

			private Function<LinkedTrackPoint, Double> createDistanceFunc(
					final DistanceUnit distanceUnit) {
				return new Function<LinkedTrackPoint, Double>() {
					public Double apply(LinkedTrackPoint wp) {
						return Double.valueOf(wp.getLink().getDistance()
								.getValue(distanceUnit));
					}
				};
			}
		};
	}

	// -----------------------------------------------------------------------------------

	public static StatCalculator<Duration> duration(final TimeUnit timeUnit) {
		return new StatCalculator<Duration>() {
			@Override
			public Duration calculate(
					Iterable<? extends LinkedTrackPoint> wayPoints) {
				double[] durations = Doubles.toArray(Lists
						.newArrayList(transform(filter(wayPoints, hasLink()),
								createTimeFunc(timeUnit))));
				return DefaultDuration.of(Primitives.sum(durations), timeUnit);
			}

			private Function<LinkedTrackPoint, Double> createTimeFunc(
					final TimeUnit timeUnit) {
				return new Function<LinkedTrackPoint, Double>() {
					public Double apply(LinkedTrackPoint wp) {
						return Double.valueOf(wp.getLink().getDuration()
								.getValue(timeUnit));
					}
				};
			}
		};
	}

	private static final Map<StatType, StatCalculator<?>> calculators = createMap();

	public static StatCalculator<?> getCalculator(StatType statType) {
		return calculators.get(statType);
	}

	private static Map<StatType, StatCalculator<?>> createMap() {
		return ImmutableMap.<StatType, StatCalculator<?>> builder()
				.put(StatType.MAX_ELE, maxEle())
				.put(StatType.MIN_ELE, minEle()).put(StatType.ASCENT, ascent())
				.put(StatType.DESCENT, descent())
				.put(StatType.MAX_SPEED, maxSpeed())
				.put(StatType.MIN_SPEED, minSpeed()).build();
	}

}
