package com.github.pfichtner.jrunalyser.base;

public final class Delegates<T> {

	private Delegates() {
		super();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getRoot(T object, Class<T> clazz) {
		T result = object;
		while (result instanceof Delegate) {
			@SuppressWarnings("rawtypes")
			Object tmp = ((Delegate) result).getDelegate();
			if (clazz.isInstance(tmp)) {
				result = (T) tmp;
			}
		}
		return result;
	}

}
