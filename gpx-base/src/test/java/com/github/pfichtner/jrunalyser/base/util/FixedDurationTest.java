package com.github.pfichtner.jrunalyser.base.util;

import static junit.framework.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.util.FixedDuration;

public class FixedDurationTest {

	@Test
	public void testFixedDuration() {
		DefaultDistance di = DefaultDistance.of(90, DistanceUnit.METERS);
		DefaultDuration du = DefaultDuration.of(30, TimeUnit.MINUTES);
		// when doing 90 meters within 30 minutes, how many meters do you do
		// within one hour?
		FixedDuration fixedDistance = new FixedDuration(DefaultDuration.of(1,
				TimeUnit.HOURS));
		assertEquals(DefaultDistance.of(180, DistanceUnit.METERS),
				fixedDistance.getDistance(di, du));
		assertEquals(DefaultDistance.of(590.57064, DistanceUnit.FEET),
				fixedDistance.getDistance(di.convertTo(DistanceUnit.FEET), du));

	}

}
