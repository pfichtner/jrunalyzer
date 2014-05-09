package com.github.pfichtner.jrunalyser.base.data;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DefaultPace;
import com.github.pfichtner.jrunalyser.base.data.DefaultSpeed;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Pace;
import com.github.pfichtner.jrunalyser.base.data.Speed;

public class SpeedPaceTest {

	@Test
	public void testSpeedConvert() {
		Speed mps = new DefaultSpeed(5.0, DistanceUnit.METERS, TimeUnit.SECONDS);
		assertEquals(18.0,
				mps.getValue(DistanceUnit.KILOMETERS, TimeUnit.HOURS), 0.0);
		assertEquals(5.0, mps.getValue(DistanceUnit.METERS, TimeUnit.SECONDS),
				0.0);
	}

	@Test
	public void testPaceConvert() {
		Pace secsPerMeter = new DefaultPace(20.0, TimeUnit.SECONDS,
				DistanceUnit.METERS);
		assertEquals(5.555,
				secsPerMeter.getValue(TimeUnit.HOURS, DistanceUnit.KILOMETERS),
				0.001);
		assertEquals(20.0,
				secsPerMeter.getValue(TimeUnit.SECONDS, DistanceUnit.METERS),
				0.0);
	}

	@Test
	public void testCrossConvert() throws Exception {
		Speed kmh = new DefaultSpeed(10.0, DistanceUnit.KILOMETERS,
				TimeUnit.HOURS);

		Pace minsPerKm = kmh.toPace(TimeUnit.MINUTES, DistanceUnit.KILOMETERS);
		assertEquals(6.0,
				minsPerKm.getValue(TimeUnit.MINUTES, DistanceUnit.KILOMETERS),
				0.0);

		assertEquals(10.0,
				minsPerKm.toSpeed(DistanceUnit.KILOMETERS, TimeUnit.HOURS)
						.getValue(DistanceUnit.KILOMETERS, TimeUnit.HOURS), 0.0);
	}

}
