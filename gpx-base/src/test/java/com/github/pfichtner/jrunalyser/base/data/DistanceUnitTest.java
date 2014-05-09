package com.github.pfichtner.jrunalyser.base.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;

public class DistanceUnitTest {

	@Test
	public void testConvert1() {
		assertEquals(Double.valueOf(1000), Double.valueOf(DistanceUnit.METERS
				.convert(1, DistanceUnit.KILOMETERS)));
		assertEquals(Double.valueOf(2), Double.valueOf(DistanceUnit.KILOMETERS
				.convert(2000, DistanceUnit.METERS)));
	}

	@Test
	public void testConvert2() {
		assertEquals(Double.valueOf(1.60934),
				Double.valueOf(DistanceUnit.KILOMETERS.convert(1,
						DistanceUnit.MILES)));
		assertEquals(Double.valueOf(5280), Double.valueOf(DistanceUnit.FEET
				.convert(1, DistanceUnit.MILES)));
	}

}
