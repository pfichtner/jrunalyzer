package com.github.pfichtner.jrunalyser.base.data;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Range.atMost;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public final class Durations {

	private Durations() {
		super();
	}

	private static final Iterable<Duration> durations = ImmutableList
			.copyOf(Ordering.natural().sortedCopy(all()));

	private static Iterable<Duration> all() {
		List<Duration> result = Lists.<Duration> newArrayList(DefaultDuration
				.of(12, TimeUnit.MINUTES));
		for (TimeUnit timeUnit : sort(EnumSet.of(TimeUnit.HOURS, TimeUnit.DAYS))) {
			for (int i : new int[] { 1, 2, 5, 10, 12 }) {
				result.add(DefaultDuration.of(i, timeUnit));
			}
		}
		return result;
	}

	private static final List<Duration> defaultDurations = sort(Lists
			.newArrayList(durationIterator(DefaultDuration
					.of(1, TimeUnit.HOURS))));

	private static <T extends Comparable<T>> List<T> sort(Iterable<T> build) {
		return ImmutableList.copyOf(Ordering.natural().sortedCopy(build));
	}

	public static Iterable<Duration> getDefaultDurations() {
		return defaultDurations;
	}

	public static Iterable<Duration> durationIterator(Duration upto) {
		return filter(durations, atMost(upto));
	}

}
