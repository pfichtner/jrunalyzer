package com.github.pfichtner.jrunalyser.base.data.track.comparator;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.comparator.AbsDiffNumberComparator;

public class AbsDiffNumberComparatorTest {

	@Test
	public void testEqual() {
		AbsDiffNumberComparator one = new AbsDiffNumberComparator(1);

		assertEquals(0, one.compare(Integer.valueOf(0), Integer.valueOf(0)));
		assertEquals(0, one.compare(Integer.valueOf(1), Integer.valueOf(0)));
		assertEquals(0, one.compare(Integer.valueOf(0), Integer.valueOf(1)));

		assertEquals(0, one.compare(Double.valueOf(0), Double.valueOf(0)));
		assertEquals(0, one.compare(Double.valueOf(1), Double.valueOf(0)));
		assertEquals(0, one.compare(Double.valueOf(0), Double.valueOf(1)));

		assertEquals(0, one.compare(Integer.valueOf(0), Double.valueOf(0)));
		assertEquals(0, one.compare(Double.valueOf(1), Integer.valueOf(0)));
		assertEquals(0, one.compare(Integer.valueOf(0), Double.valueOf(1)));
	}

	@Test
	public void testNotEqual() {
		AbsDiffNumberComparator one = new AbsDiffNumberComparator(1);

		assertTrue(one.compare(Integer.valueOf(2), Integer.valueOf(0)) > 0);
		assertTrue(one.compare(Integer.valueOf(0), Integer.valueOf(2)) < 0);
	}

}
