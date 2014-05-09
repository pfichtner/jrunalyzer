package com.github.pfichtner.jrunalyser.base.util;

import com.google.common.base.Predicate;

public final class Primitives {

	private Primitives() {
		super();
	}

	public static int sum(int[] array) {
		int result = 0;
		for (int i : array) {
			result += i;
		}
		return result;
	}

	public static int avg(int[] ints) {
		return sum(ints) / ints.length;
	}

	// -----------------------------------------------------

	public static double sum(double[] doubles) {
		double result = 0;
		for (double d : doubles) {
			result += d;
		}
		return result;
	}

	public static double sum(double[] doubles, Predicate<Number> predicate) {
		double sum = 0;
		double prev = Double.NaN;
		for (double number : doubles) {
			if (!Double.isNaN(prev)) {
				double diff = number - prev;
				if (predicate.apply(Double.valueOf(diff))) {
					sum += diff;
				}
			}
			prev = number;
		}
		return sum;
	}

	public static double avg(double[] doubles) {
		return sum(doubles) / doubles.length;
	}

	// -----------------------------------------------------

}
