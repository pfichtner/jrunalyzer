package com.github.pfichtner.jrunalyser.base.data;

import java.util.concurrent.TimeUnit;

import com.google.common.collect.Ordering;

public final class Speeds {

	public static class SpeedComparator {

		public class Result {

			private final Speed comparedTo;

			public Result(Speed comparedTo) {
				this.comparedTo = comparedTo;
			}

			public double asMultiplicator() {
				Speed s1 = this.comparedTo;
				Speed s2 = SpeedComparator.this.reference;

				DistanceUnit lowerDu = lowerDu(s1, s2);
				TimeUnit lowerTu = lowerTu(s1, s2);
				return s2.getValue(lowerDu, lowerTu)
						/ s1.getValue(lowerDu, lowerTu);
			}

			private TimeUnit lowerTu(Speed s1, Speed s2) {
				return min(s1.getTimeUnit(), s2.getTimeUnit());
			}

			private DistanceUnit lowerDu(Speed s1, Speed s2) {
				return min(s1.getDistanceUnit(), s2.getDistanceUnit());
			}

			private <T extends Comparable<T>> T min(T val1, T tu2) {
				return Ordering.natural().min(val1, tu2);
			}

			public int inPercent() {
				return (int) (asMultiplicator() * 100 - 100);
			}

		}

		private final Speed reference;

		public SpeedComparator(Speed reference) {
			this.reference = reference;
		}

		public Result fasterThan(Speed compareTo) {
			return new Result(compareTo);
		}

	}

	private Speeds() {
		super();
	}

	public static SpeedComparator is(Speed reference) {
		return new SpeedComparator(reference);
	}

}
