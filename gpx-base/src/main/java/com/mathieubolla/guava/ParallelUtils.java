package com.mathieubolla.guava;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.FluentIterable.from;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.AbstractIterator;

public final class ParallelUtils {

	private ParallelUtils() {
		super();
	}

	/**
	 * Will create a fixed thread pool executor service, and shut it down at
	 * iterator's end.
	 * 
	 * @see #parallelTransform(Iterable, com.google.common.base.Function, int,
	 *      java.util.concurrent.ExecutorService)
	 */
	public static <T, U> Iterable<U> parallelTransform(
			final Iterable<T> source, final Function<? super T, U> transform,
			int threads) {
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		return doTransformStuf(source, transform, threads, executorService,
				true);
	}

	/**
	 * Computes transform on source, threads elements at a time, and iterates
	 * over these in source order, tapping into executorService threadPool.
	 */
	public static <T, U> Iterable<U> parallelTransform(
			final Iterable<T> source, final Function<? super T, U> transform,
			int threads, final ExecutorService executorService) {
		return doTransformStuf(source, transform, threads, executorService,
				false);
	}

	/**
	 * Computes filter on source, threads elements at a time, and iterates over
	 * these in source order, tapping into executorService threadPool.
	 */
	public static <T> Iterable<T> parallelFilter(Iterable<T> source,
			Predicate<? super T> predicate, int threads,
			ExecutorService executorService) {
		return doFilterStuf(source, predicate, threads, executorService, false);
	}

	/**
	 * Will create a fixed thread pool executor service, and shut it down at
	 * iterator's end.
	 * 
	 * @see #parallelFilter(Iterable, com.google.common.base.Predicate, int,
	 *      java.util.concurrent.ExecutorService)
	 */
	public static <T> Iterable<T> parallelFilter(Iterable<T> source,
			Predicate<? super T> predicate, int threads) {
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		return doFilterStuf(source, predicate, threads, executorService, true);
	}

	private static <T, U> Iterable<U> doTransformStuf(final Iterable<T> source,
			final Function<? super T, U> transform, int threads,
			final ExecutorService executorService,
			final boolean shutdownInTheEnd) {

		checkArgument(threads > 0,
				"amount of threads must be strictly positive");

		final LinkedBlockingQueue<Future<U>> queue = new LinkedBlockingQueue<Future<U>>(
				threads);
		final Iterator<T> sourceIterator = source.iterator();

		return new Iterable<U>() {
			public Iterator<U> iterator() {
				return new AbstractIterator<U>() {
					@Override
					protected U computeNext() {
						if (queue.isEmpty() && !sourceIterator.hasNext()) {
							if (shutdownInTheEnd) {
								executorService.shutdown();
							}
							return endOfData();
						}

						while (queue.remainingCapacity() > 0
								&& sourceIterator.hasNext()) {
							final T next = sourceIterator.next();
							Future<U> future = executorService
									.submit(new Callable<U>() {
										public U call() throws Exception {
											return transform.apply(next);
										}
									});
							try {
								queue.put(future);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								throw Throwables.propagate(e);
							}
						}

						try {
							return queue.take().get();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							throw Throwables.propagate(e);
						} catch (ExecutionException e) {
							Throwable orig = e.getCause();
							Throwables.propagateIfPossible(orig);
							throw Throwables.propagate(orig);
						}
					}
				};
			}
		};
	}

	private static <T> Iterable<T> doFilterStuf(Iterable<T> source,
			Predicate<? super T> predicate, int threads,
			ExecutorService executorService, boolean shutdownInTheEnd) {
		return from(
				doTransformStuf(source, calculatePredicateResult(predicate),
						threads, executorService, shutdownInTheEnd)).filter(
				FilterResult.getState).transform(
				ParallelUtils.<T> getGetObjectFunction());
	}

	@SuppressWarnings("unchecked")
	private static <T> Function<FilterResult, T> getGetObjectFunction() {
		return (Function<FilterResult, T>) FilterResult.getObject;
	}

	private static <T> Function<T, FilterResult> calculatePredicateResult(
			final Predicate<T> predicate) {
		return new Function<T, FilterResult>() {
			public FilterResult apply(T input) {
				return new FilterResult(input, predicate.apply(input));
			}
		};
	}

	private static class FilterResult {

		private static final Function<FilterResult, Object> getObject = new Function<FilterResult, Object>() {
			@Override
			public Object apply(FilterResult filterResult) {
				return filterResult.object;
			}
		};

		private static final Predicate<FilterResult> getState = new Predicate<FilterResult>() {
			public boolean apply(FilterResult filterResult) {
				return filterResult.state;
			};
		};

		private final Object object;
		private final boolean state;

		FilterResult(Object a, boolean b) {
			this.object = a;
			this.state = b;
		}
	}

}
