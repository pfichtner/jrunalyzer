package com.github.pfichtner.jrunalyser.base.data.stat;

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.floater.HighlightableSegment;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.google.common.base.Predicate;

public final class Predicates {

	private Predicates() {
		super();
	}

	public static final Predicate<Double> isValid = not(new Predicate<Double>() {
		public boolean apply(Double value) {
			return value.isNaN() || value.isInfinite();
		}
	});

	public static final Predicate<Speed> isValidSpeed = compose(
			Predicates.isValid, Functions.Speeds.value());

	public static final Predicate<LinkedTrackPoint> hasValidSpeed = compose(
			Predicates.isValid,
			compose(Functions.Speeds.value(), Functions.LinkedWayPoints.speed()));

	// ---------------------------------------------------------------------

	public static final class WayPoints {
		private WayPoints() {
			super();
		}

		public static Predicate<? super WayPoint> hasElevation() {
			return compose(notNull(), Functions.WayPoints.elevation());
		}

	}

	public static final class LinkedWayPoints {
		private LinkedWayPoints() {
			super();
		}

		private static final Predicate<LinkedTrackPoint> hasLink = compose(
				notNull(), Functions.LinkedWayPoints.link());

		private static final Predicate<Number> absGE0dot4 = new Predicate<Number>() {
			@Override
			public boolean apply(Number number) {
				return Math.abs(number.doubleValue()) >= 0.4;
			}
		};
		private static final Predicate<Number> hasPositiveElevationDiff = com.google.common.base.Predicates
				.and(new Predicate<Number>() {
					@Override
					public boolean apply(Number number) {
						return number.doubleValue() > 0;
					}
				}, absGE0dot4);
		private static final Predicate<Number> hasNegativeElevationDiff = com.google.common.base.Predicates
				.and(new Predicate<Number>() {
					@Override
					public boolean apply(Number number) {
						return number.doubleValue() < 0;
					}
				}, absGE0dot4);

		public static Predicate<LinkedTrackPoint> hasLink() {
			return hasLink;
		}

		public static Predicate<Number> hasNegativeElevationdiff() {
			return hasNegativeElevationDiff;
		}

		public static Predicate<Number> hasPositiveElevationdiff() {
			return hasPositiveElevationDiff;
		}

	}

	public static final class Segments {
		public static final Predicate<Segment> isHighligted = new Predicate<Segment>() {
			@Override
			public boolean apply(Segment segment) {
				return segment instanceof HighlightableSegment
						&& ((HighlightableSegment) segment).isHighligted();
			}
		};
	}

	public static final class HighlightableSegments {
		public static final Predicate<HighlightableSegment> isHighligted = new Predicate<HighlightableSegment>() {
			@Override
			public boolean apply(HighlightableSegment segment) {
				return segment.isHighligted();
			}
		};
	}

}
