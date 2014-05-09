package com.github.pfichtner.jrunalyser.ui.dock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.google.common.base.Supplier;

public final class Suppliers {

	private Suppliers() {
		super();
	}

	private static class BackgroundInvocationHandler<T> implements
			InvocationHandler {

		private final FutureTask<T> futureTask;

		private T real;

		public BackgroundInvocationHandler(final Supplier<T> supplier) {
			this.futureTask = new FutureTask<T>(new Callable<T>() {
				public T call() {
					return supplier.get();
				}
			});
			Executors.newSingleThreadExecutor().execute(this.futureTask);
		}

		public Object invoke(final Object proxy, final Method method,
				final Object[] args) throws Throwable {
			try {
				// assign for performance reasons, no synchronization needed:
				// multiple assignment of this.real won't hurt
				if (this.real == null) {
					// this call will block if the object was not already
					// created
					this.real = this.futureTask.get();
				}
				return method.invoke(this.real, args);
			} catch (final InvocationTargetException ite) {
				throw ite.getCause();
			}
		}
	}

	/**
	 * Creates a Proxy that calls the passed Supplier to retrieve the delegate.
	 * All calls to the Proxy will block until the passed Supplier has created
	 * the delegate-object.
	 * 
	 * @param <T>
	 *            type of Proxy
	 * @param delegateSupplier
	 *            the Supplier to provides the delegate-object
	 * @param iface
	 *            interfaces to implement
	 * @return Proxy of type <code>ifaces</code> that retrieves the
	 *         delegate-object in background
	 */
	public static <T> T background(final Supplier<T> delegateSupplier,
			final Class<T> iface) {
		return iface.cast(Proxy.newProxyInstance(delegateSupplier.getClass()
				.getClassLoader(), new Class[] { iface },
				new BackgroundInvocationHandler<T>(delegateSupplier)));
	}

}
