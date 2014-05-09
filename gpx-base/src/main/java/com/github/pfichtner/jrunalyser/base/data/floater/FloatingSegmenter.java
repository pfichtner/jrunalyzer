package com.github.pfichtner.jrunalyser.base.data.floater;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Range.greaterThan;
import static com.google.common.collect.Range.lessThan;

import java.util.Collections;
import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenter;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.stat.StatCalculators.StatCalculator;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public class FloatingSegmenter<S extends Comparable<S>, T extends Comparable<T>>
		implements Segmenter {

	private static class Snapshot<T> {

		private final T value;
		private final int start;
		private final int end;

		public Snapshot(T value, int start, int end) {
			this.value = value;
			this.start = start;
			this.end = end;
		}

		public T getValue() {
			return this.value;
		}

		public int getStart() {
			return this.start;
		}

		public int getEnd() {
			return this.end;
		}

	}

	public static final Predicate<Integer> GREATEST = greaterThan(Integer.valueOf(0));

	public static final Predicate<Integer> LOWEST = lessThan(Integer.valueOf(0));

	public static final Predicate<Integer> EQUAL = equalTo(Integer.valueOf(0));

	private final S value;
	private final StatCalculator<S> floatingValueCalculator;
	private final StatCalculator<T> valueCalculator;
	private final Predicate<Integer> predicate;

	public FloatingSegmenter(S value,
			StatCalculator<S> floatingValueCalculator,
			StatCalculator<T> valueCalculator) {
		this(value, floatingValueCalculator, valueCalculator, GREATEST);
	}

	public FloatingSegmenter(S value,
			StatCalculator<S> floatingValueCalculator,
			StatCalculator<T> valueCalculator, Predicate<Integer> predicate) {
		this.value = value;
		this.floatingValueCalculator = floatingValueCalculator;
		this.valueCalculator = valueCalculator;
		this.predicate = predicate;
	}

	@Override
	public Track segment(Track track) {
		List<? extends LinkedTrackPoint> trkpts = track.getTrackpoints();
		Snapshot<T> snapshot = null;
		List<? extends LinkedTrackPoint> view;
		int start = 0;
		for (int end = 1; end < trkpts.size(); end++) {
			view = trkpts.subList(start, end);
			S actValue = this.floatingValueCalculator.calculate(view);
			// distance/duration reached?
			if (actValue.compareTo(this.value) >= 0) {
				T calculated = this.valueCalculator.calculate(view);
				// calculated value > oldValue?
				if (snapshot == null
						|| this.predicate.apply(Integer.valueOf(calculated
								.compareTo(snapshot.getValue())))) {
					snapshot = new Snapshot<T>(calculated, start, end);
				}
				// increase startpos until distance/duration is no more
				// acceptable
				while (actValue.compareTo(this.value) >= 0) {
					view = trkpts.subList(++start, end);
					actValue = this.floatingValueCalculator.calculate(view);
				}
			}
		}

		List<Segment> segments = snapshot == null ? Collections
				.<Segment> singletonList(new DefaultHighlightableSegment(
						trkpts, false, DefaultStatistics.ofWaypoints(trkpts)))
				: createResult(trkpts, snapshot);
		return new DefaultTrack(track.getId(), track.getMetadata(),
				track.getWaypoints(), segments, track.getStatistics());
	}

	private List<Segment> createResult(List<? extends LinkedTrackPoint> wps,
			Snapshot<T> snapshot) {
		List<Segment> segs = Lists.newArrayList();

		if (snapshot.getStart() > 0) {
			List<? extends LinkedTrackPoint> nwps = wps.subList(0,
					snapshot.getStart());
			segs.add(new DefaultHighlightableSegment(nwps, false,
					DefaultStatistics.ofWaypoints(nwps)));
		}

		{
			List<? extends LinkedTrackPoint> nwps = wps.subList(
					snapshot.getStart(), snapshot.getEnd());
			segs.add(new DefaultHighlightableSegment(nwps, true,
					DefaultStatistics.ofWaypoints(nwps)));
		}

		if (snapshot.getEnd() + 1 < wps.size() - 1) {
			List<? extends LinkedTrackPoint> nwps = wps.subList(
					snapshot.getEnd() + 1, wps.size() - 1);
			segs.add(new DefaultHighlightableSegment(nwps, false,
					DefaultStatistics.ofWaypoints(nwps)));
		}

		return segs;
	}

}
