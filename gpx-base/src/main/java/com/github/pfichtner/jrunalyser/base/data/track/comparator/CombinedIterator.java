package com.github.pfichtner.jrunalyser.base.data.track.comparator;

import java.util.Iterator;

import com.google.common.base.Function;

public class CombinedIterator<T, E> implements Iterator<E> {

	public static class Pair<T> {
		private final T value1;
		private final T value2;

		public Pair(T value1, T value2) {
			this.value1 = value1;
			this.value2 = value2;
		}

		public T getValue1() {
			return this.value1;
		}

		public T getValue2() {
			return this.value2;
		}

	}

	private final Iterator<? extends T> it1, it2;
	private final Function<Pair<T>, E> reduceFunction;

	public CombinedIterator(Iterator<? extends T> it1, Iterator<? extends T> it2,
			Function<Pair<T>, E> reduceFunction) {
		this.it1 = it1;
		this.it2 = it2;
		this.reduceFunction = reduceFunction;
	}

	@Override
	public boolean hasNext() {
		return this.it1.hasNext() && this.it2.hasNext();
	}

	public boolean isTotallyCollected() {
		return !this.it1.hasNext() && !this.it2.hasNext();
	}

	@Override
	public E next() {
		return this.reduceFunction.apply(new Pair<T>(this.it1.next(), this.it2
				.next()));
	}

	@Override
	public void remove() {
		this.it1.remove();
		this.it2.remove();
	}

	@Override
	public String toString() {
		return "CombinedIterator [it1=" + this.it1 + ", it2=" + this.it2
				+ ", reduceFunction=" + this.reduceFunction + "]";
	}

}