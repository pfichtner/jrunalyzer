package com.github.pfichtner.jrunalyser.base.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Distances;
import com.google.common.collect.Lists;

public class DistancesTest {

	@Test
	public void test500m() {
		Distance upto = DefaultDistance.of(500, DistanceUnit.METERS);
		assertEquals(Lists.newArrayList(DefaultDistance.of(400,
				DistanceUnit.METERS)), Lists.newArrayList(Distances
				.distanceIterator(upto)));
	}

	@Test
	public void test900m() {
		Distance upto = DefaultDistance.of(900, DistanceUnit.METERS);
		assertEquals(Lists.newArrayList(
				DefaultDistance.of(400, DistanceUnit.METERS),
				DefaultDistance.of(1D / 2, DistanceUnit.MILES)),
				Lists.newArrayList(Distances.distanceIterator(upto)));
	}

	@Test
	public void test1000m() {
		Distance upto = DefaultDistance.of(1000, DistanceUnit.METERS);
		assertEquals(Lists.newArrayList(
				DefaultDistance.of(400, DistanceUnit.METERS),
				DefaultDistance.of(1D / 2, DistanceUnit.MILES),
				DefaultDistance.of(1, DistanceUnit.KILOMETERS)),
				Lists.newArrayList(Distances.distanceIterator(upto)));
	}

	@Test
	public void test1100m() {
		Distance upto = DefaultDistance.of(1100, DistanceUnit.METERS);
		assertEquals(Lists.newArrayList(
				DefaultDistance.of(400, DistanceUnit.METERS),
				DefaultDistance.of(1D / 2, DistanceUnit.MILES),
				DefaultDistance.of(1, DistanceUnit.KILOMETERS)),
				Lists.newArrayList(Distances.distanceIterator(upto)));
	}

	@Test
	public void testAbs() {
		assertEquals(DefaultDistance.of(5, DistanceUnit.METERS),
				Distances.abs(DefaultDistance.of(5, DistanceUnit.METERS)));
		assertEquals(DefaultDistance.of(5, DistanceUnit.METERS),
				Distances.abs(DefaultDistance.of(-5, DistanceUnit.METERS)));
	}

}
