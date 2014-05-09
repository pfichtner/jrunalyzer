package com.mathieubolla.guava;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

public abstract class FluentParallelIterable<E> implements Iterable<E> {

	private final FluentIterable<E> delegate;

	private final int threads;

	private final ExecutorService executorService;

	private boolean parallel = true;

	private FluentParallelIterable(Iterable<E> delegate, int threads,
			ExecutorService executorService) {
		this.delegate = FluentIterable.from(delegate);
		this.threads = threads;
		this.executorService = executorService;
	}

	public static <E> FluentParallelIterable<E> from(final Iterable<E> source) {
		int threads = Runtime.getRuntime().availableProcessors();
		return from(source, threads, Executors.newFixedThreadPool(threads));
	}

	public static <E> FluentParallelIterable<E> from(final Iterable<E> source,
			int threads, ExecutorService executorService) {
		return source instanceof FluentParallelIterable ? (FluentParallelIterable<E>) source
				: new FluentParallelIterable<E>(source, threads,
						executorService) {
					public Iterator<E> iterator() {
						return source.iterator();
					}
				};
	}

	public final boolean allMatch(Predicate<? super E> predicate) {
		// TODO do check this.parallel
		return this.delegate.allMatch(predicate);
	}

	public final boolean anyMatch(Predicate<? super E> predicate) {
		// TODO do check this.parallel
		return this.delegate.anyMatch(predicate);
	}

	public final boolean contains(Object element) {
		return this.delegate.contains(element);
	}

	public final <C extends Collection<? super E>> C copyInto(C arg0) {
		return this.delegate.copyInto(arg0);
	}

	public final FluentParallelIterable<E> cycle() {
		return from(this.delegate.cycle(), this.threads, this.executorService);
	}

	public final <T> FluentParallelIterable<T> filter(Class<T> type) {
		return from(this.delegate.filter(type), this.threads,
				this.executorService);
	}

	public final Optional<E> first() {
		return this.delegate.first();
	}

	public final Optional<E> firstMatch(Predicate<? super E> predicate) {
		// TODO do check this.parallel
		return this.delegate.firstMatch(predicate);
	}

	public final E get(int position) {
		return this.delegate.get(position);
	}

	public final <K> ImmutableListMultimap<K, E> index(
			Function<? super E, K> keyFunction) {
		return this.delegate.index(keyFunction);
	}

	public final boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	public Iterator<E> iterator() {
		return this.delegate.iterator();
	}

	public final Optional<E> last() {
		return this.delegate.last();
	}

	public final FluentParallelIterable<E> limit(int size) {
		return from(this.delegate.limit(size), this.threads,
				this.executorService);
	}

	public final int size() {
		return this.delegate.size();
	}

	public final FluentParallelIterable<E> skip(int numberToSkip) {
		return from(this.delegate.skip(numberToSkip), this.threads,
				this.executorService);
	}

	public final E[] toArray(Class<E> type) {
		return this.delegate.toArray(type);
	}

	public final ImmutableList<E> toList() {
		return this.delegate.toList();
	}

	public final <V> ImmutableMap<E, V> toMap(
			Function<? super E, V> valueFunction) {
		return this.delegate.toMap(valueFunction);
	}

	public final ImmutableSet<E> toSet() {
		return this.delegate.toSet();
	}

	public final ImmutableList<E> toSortedList(Comparator<? super E> comparator) {
		return this.delegate.toSortedList(comparator);
	}

	public final ImmutableSortedSet<E> toSortedSet(
			Comparator<? super E> comparator) {
		return this.delegate.toSortedSet(comparator);
	}

	public <T> FluentParallelIterable<T> transformAndConcat(
			Function<? super E, ? extends Iterable<T>> function) {
		return from(this.delegate.transformAndConcat(function), this.threads,
				this.executorService);
	}

	public final <K> ImmutableMap<K, E> uniqueIndex(
			Function<? super E, K> keyFunction) {
		return this.delegate.uniqueIndex(keyFunction);
	}

	// -----------------------------------------------------------------------------------

	public FluentParallelIterable<E> parallel() {
		this.parallel = true;
		return this;
	}

	public FluentParallelIterable<E> reduce() {
		this.parallel = false;
		return this;
	}

	public final <T> FluentParallelIterable<T> transform(
			Function<? super E, T> function) {
		Iterable<T> source = this.parallel ? ParallelUtils.parallelTransform(
				this.delegate, function, this.threads, this.executorService)
				: this.delegate.transform(function);
		return from(source, this.threads, this.executorService);
	}

	public final FluentParallelIterable<E> filter(Predicate<? super E> predicate) {
		Iterable<E> source = this.parallel ? ParallelUtils.parallelFilter(
				this.delegate, predicate, this.threads, this.executorService)
				: this.delegate.filter(predicate);
		return from(source, this.threads, this.executorService);
	}

}
