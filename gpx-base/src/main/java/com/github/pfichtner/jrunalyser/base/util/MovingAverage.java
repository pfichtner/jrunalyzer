package com.github.pfichtner.jrunalyser.base.util;

import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class MovingAverage {

	private final int period;
	private int pos;
	private double[] window = new double[0];
	private double sum;

	public MovingAverage(@Nonnegative int period) {
		this.period = period;
		checkArgument(period > 0, "Period must be a positive integer");
	}

	public double add(double num) {
		boolean resizeNeeded = this.pos >= this.window.length
				&& this.pos < this.period;
		this.sum += num; // add new value
		if (resizeNeeded) {
			resize();
		} else {
			this.sum -= this.window[this.pos]; // subtract old value
		}
		this.window[this.pos] = num;
		if (++this.pos == this.period) {
			this.pos = 0;
		}
		return getValue();
	}

	public double getValue() {
		return this.sum / this.window.length;
	}

	private void resize() {
		double[] tmp = new double[this.window.length + 1];
		System.arraycopy(this.window, 0, tmp, 0, this.window.length);
		this.window = tmp;
	}

}