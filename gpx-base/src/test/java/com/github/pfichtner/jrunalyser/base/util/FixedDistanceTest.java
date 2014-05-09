package com.github.pfichtner.jrunalyser.base.util;

import static junit.framework.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.util.FixedDistance;

public class FixedDistanceTest {

	@Test
	public void testFixedDistance() {
		DefaultDistance di = DefaultDistance.of(600, DistanceUnit.METERS);
		DefaultDuration du = DefaultDuration.of(3, TimeUnit.MINUTES);
		// when doing 600 meters within 3 minutes, how long does it take to do
		// 800 meters?
		FixedDistance fixedDistance = new FixedDistance(DefaultDistance.of(800,
				DistanceUnit.METERS));
		assertEquals(DefaultDuration.of(4, TimeUnit.MINUTES),
				fixedDistance.getDuration(di, du));
		assertEquals(DefaultDuration.of(240, TimeUnit.SECONDS),
				fixedDistance.getDuration(di, du.convertTo(TimeUnit.SECONDS)));

	}

}
