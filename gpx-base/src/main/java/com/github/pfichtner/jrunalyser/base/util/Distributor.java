package com.github.pfichtner.jrunalyser.base.util;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public final class Distributor {

	private Distributor() {
		super();
	}

	public static <T> List<Collection<T>> distribute(Collection<T> in, int size) {
		List<Collection<T>> result = Lists.newArrayListWithExpectedSize(size);
		for (int i = 0; i < size; i++) {
			result.add(Lists.<T> newArrayList());
		}
		int index = 0;
		for (T t : in) {
			result.get(index++ % size).add(t);
		}
		return result;
	}

}
