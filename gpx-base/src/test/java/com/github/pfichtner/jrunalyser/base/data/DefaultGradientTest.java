package com.github.pfichtner.jrunalyser.base.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.DefaultGradient;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Gradient;

public class DefaultGradientTest {

	@Test
	public void testPositive() {
		Distance d = DefaultDistance.of(1, DistanceUnit.KILOMETERS);
		Distance h = DefaultDistance.of(1, DistanceUnit.METERS);
		Gradient gradient = DefaultGradient.of(d, h);
		assertEquals(0.1, gradient.convertTo(DistanceUnit.METERS).getValue(),
				0.001);
		assertEquals(0.0001, gradient.convertTo(DistanceUnit.KILOMETERS)
				.getValue(), 0.001);
	}

	@Test
	public void testPositive2() {
		Distance d = DefaultDistance.of(100, DistanceUnit.METERS);
		Distance h = DefaultDistance.of(50, DistanceUnit.METERS);
		Gradient gradient = DefaultGradient.of(d, h);
		assertEquals(50, gradient.convertTo(DistanceUnit.METERS).getValue(),
				0.001);
		assertEquals(0.05, gradient.convertTo(DistanceUnit.KILOMETERS)
				.getValue(), 0.001);
	}

	@Test
	public void testNegative() {
		Distance d = DefaultDistance.of(1, DistanceUnit.KILOMETERS);
		Distance h = DefaultDistance.of(-1, DistanceUnit.METERS);
		Gradient gradient = DefaultGradient.of(d, h);
		assertEquals(-0.1, gradient.convertTo(DistanceUnit.METERS).getValue(),
				0.001);
		assertEquals(-0.0001, gradient.convertTo(DistanceUnit.KILOMETERS)
				.getValue(), 0.001);
	}

	@Test
	public void testNegative1() {
		Distance d = DefaultDistance.of(100, DistanceUnit.METERS);
		Distance h = DefaultDistance.of(-50, DistanceUnit.METERS);
		Gradient gradient = DefaultGradient.of(d, h);
		assertEquals(-50, gradient.convertTo(DistanceUnit.METERS).getValue(),
				0.001);
		assertEquals(-0.05, gradient.convertTo(DistanceUnit.KILOMETERS)
				.getValue(), 0.001);
	}

}
