package com.github.pfichtner.jrunalyser.base.data.track.comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.comparator.RelDiffNumberComparator;

public class RelDiffNumberComparatorTest {

	@Test
	public void testEqual() {
		RelDiffNumberComparator ten = new RelDiffNumberComparator(10);
		assertEquals(0, ten.compare(Integer.valueOf(0), Integer.valueOf(0)));
		assertEquals(0, ten.compare(Integer.valueOf(100), Integer.valueOf(90)));
		assertEquals(0, ten.compare(Integer.valueOf(90), Integer.valueOf(100)));
	}

	@Test
	public void testNotEqual() {
		RelDiffNumberComparator ten = new RelDiffNumberComparator(10);

		assertPositive(ten.compare(Integer.valueOf(1), Integer.valueOf(0)));
		assertNegative(ten.compare(Integer.valueOf(0), Integer.valueOf(1)));
		
		assertPositive(ten.compare(Integer.valueOf(100), Integer.valueOf(89)));
		assertNegative(ten.compare(Integer.valueOf(89), Integer.valueOf(100)));

	}

	private void assertPositive(int value) {
		assertTrue(String.valueOf(value), value > 0);
	}

	private void assertNegative(int value) {
		assertTrue(String.valueOf(value), value < 0);
	}

}
