package com.github.pfichtner.jrunalyser.base.data;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Range.atMost;

import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public final class Distances {

	private Distances() {
		super();
	}

	private static final Distance ZERO = DefaultDistance.of(0,
			DistanceUnit.METERS);

	private static final Iterable<Distance> distances = ImmutableList
			.copyOf(Ordering.natural().sortedCopy(all()));

	private static Iterable<Distance> all() {
		List<Distance> result = Lists.<Distance> newArrayList(
				DefaultDistance.of(400, DistanceUnit.METERS),
				DefaultDistance.of(0.5, DistanceUnit.MILES));
		for (DistanceUnit distanceUnit : sort(EnumSet.of(
				DistanceUnit.KILOMETERS, DistanceUnit.MILES))) {
			for (int i : new int[] { 1, 2, 5, 10, 25, 50, 100, 250, 500, 1000,
					2500, 5000 }) {
				result.add(DefaultDistance.of(i, distanceUnit));
			}
		}
		return result;
	}

	private static final List<Distance> defaultDistances = sort(Lists
			.newArrayList(distanceIterator(DefaultDistance.of(10,
					DistanceUnit.MILES))));

	private static <T extends Comparable<T>> List<T> sort(Iterable<T> build) {
		return ImmutableList.copyOf(Ordering.natural().sortedCopy(build));
	}

	public static Iterable<Distance> getDefaultDistances() {
		return defaultDistances;
	}

	public static Iterable<Distance> distanceIterator(Distance upto) {
		return filter(distances, atMost(upto));
	}

	public static Distance abs(Distance distance) {
		return distance.compareTo(ZERO) >= 0 ? distance : reverse(distance);
	}

	public static Distance reverse(Distance distance) {
		DistanceUnit du = distance.getDistanceUnit();
		return DefaultDistance.of(0 - distance.getValue(du), du);
	}

}
