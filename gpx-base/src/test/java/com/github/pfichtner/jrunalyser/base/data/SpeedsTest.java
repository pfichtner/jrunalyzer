package com.github.pfichtner.jrunalyser.base.data;

import static com.github.pfichtner.jrunalyser.base.data.DistanceUnit.KILOMETERS;
import static com.github.pfichtner.jrunalyser.base.data.Speeds.is;
import static java.util.concurrent.TimeUnit.HOURS;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.Speeds.SpeedComparator.Result;

public class SpeedsTest {

	private static final Speed faster = new DefaultSpeed(DefaultDistance.of(10,
			KILOMETERS), DefaultDuration.of(1, HOURS));
	private static final Speed slower = new DefaultSpeed(DefaultDistance.of(10,
			KILOMETERS), DefaultDuration.of(90, TimeUnit.MINUTES));

	@Test
	public void testFasterThan() {
		Result r1 = is(faster).fasterThan(slower);
		assertEquals(1.5, r1.asMultiplicator(), 0.0);
		assertEquals(50, r1.inPercent());

		Result r2 = is(slower).fasterThan(faster);
		assertEquals(0.6666666666666666, r2.asMultiplicator(), 0.0);
		assertEquals(-33, r2.inPercent());
	}

}
