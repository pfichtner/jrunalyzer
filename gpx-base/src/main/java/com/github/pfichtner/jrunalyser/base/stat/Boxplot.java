package com.github.pfichtner.jrunalyser.base.stat;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Range;
import com.google.common.primitives.Doubles;

public final class Boxplot {

	private final double[] values;

	public static class InterquatileRange {

		private final double lower;
		private final double upper;

		public InterquatileRange(double lower, double upper) {
			this.lower = lower;
			this.upper = upper;
		}

		public double getLower() {
			return this.lower;
		}

		public double getUpper() {
			return this.upper;
		}

		@Override
		public String toString() {
			return "InterquatileRange [lower=" + this.lower + ", upper="
					+ this.upper + "]";
		}

	}

	public Boxplot(Double[] values) {
		this(Doubles.toArray(Arrays.asList(values)));
	}

	public Boxplot(Collection<Double> values) {
		this(Doubles.toArray(values));
	}

	public Boxplot(double[] values) {
		this.values = sort(values.clone());
	}

	private static double[] sort(double[] values) {
		Arrays.sort(values);
		return values;
	}

	public double median() {
		return m(2);
	}

	public double q1() {
		return m(4);
	}

	public double q3() {
		return m(1.25);
	}

	private double m(double q) {
		int c = (int) (this.values.length / q);
		double m = this.values[c];
		return this.values.length % 2 == 0 ? (this.values[c - 1] + m) / 2 : m;
	}

	public InterquatileRange innerFences() {
		return fences(1.5);
	}

	public InterquatileRange outerFences() {
		return fences(3.0);
	}

	public InterquatileRange fences(double m) {
		double q3 = q3();
		double q1 = q1();
		double iqr = (q3 - q1) * m;
		return new InterquatileRange(q1 - iqr, q3 + iqr);
	}

	public double[] getValues() {
		return this.values.clone();
	}

	public double[] getValues(InterquatileRange fence) {
		return filter(Range.closed(Double.valueOf(fence.getLower()),
				Double.valueOf(fence.getUpper())));
	}

	public double[] filter(Predicate<Double> p) {
		return Doubles.toArray(FluentIterable.from(Doubles.asList(this.values))
				.filter(p).toList());
	}

}
