package com.github.pfichtner.jrunalyser.base.util;

import java.util.Iterator;

public class MovingAverageIterator implements Iterator<Double> {

	private final Iterator<? extends Number> delegate;
	private final MovingAverage movingAverage;

	public MovingAverageIterator(Iterator<? extends Number> delegate, int period) {
		this.delegate = delegate;
		this.movingAverage = new MovingAverage(period);
	}

	@Override
	public boolean hasNext() {
		return this.delegate.hasNext();
	}

	@Override
	public Double next() {
		Number next = this.delegate.next();
		return Double.valueOf(next == null ? this.movingAverage.getValue()
				: this.movingAverage.add(next.doubleValue()));
	}

	@Override
	public void remove() {
		this.delegate.remove();
	}

}
