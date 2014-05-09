package com.github.pfichtner.jrunalyser.base.data;

public interface MathObject<T> extends Comparable<T> {

	T add(T other);

	T subtract(T other);

	T divide(double divider);

	T multiply(double multiplier);

}
