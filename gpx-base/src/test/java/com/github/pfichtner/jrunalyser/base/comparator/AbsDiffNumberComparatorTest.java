package com.github.pfichtner.jrunalyser.base.comparator;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.comparator.AbsDiffNumberComparator;

public class AbsDiffNumberComparatorTest {

	private static final Integer zero = Integer.valueOf(0);

	private static final Integer p10 = Integer.valueOf(10);
	private static final Integer n10 = Integer.valueOf(-10);

	private static final Integer p11 = Integer.valueOf(11);
	private static final Integer n11 = Integer.valueOf(-11);

	private static final Integer maxVal = Integer.valueOf(Integer.MAX_VALUE);
	private static final Integer minVal = Integer.valueOf(Integer.MIN_VALUE);

	// values can differ up to 10 (absolut)
	private final AbsDiffNumberComparator comparator = new AbsDiffNumberComparator(
			10);

	@Test
	public void testCompare_resultMustBeZero() {
		assertEquals(0, this.comparator.compare(zero, zero));
		assertEquals(0, this.comparator.compare(p10, zero));
		assertEquals(0, this.comparator.compare(zero, p10));
		assertEquals(0, this.comparator.compare(n10, zero));
		assertEquals(0, this.comparator.compare(zero, n10));
	}

	@Test
	public void testCompare_resultMustBeNonZero() {
		assertTrue(this.comparator.compare(p11, zero) > 0);
		assertTrue(this.comparator.compare(zero, p11) < 0);
		assertTrue(this.comparator.compare(n11, zero) < 0);
		assertTrue(this.comparator.compare(zero, n11) > 0);

		assertTrue(this.comparator.compare(maxVal, zero) > 0);
		assertTrue(this.comparator.compare(zero, maxVal) < 0);
		assertTrue(this.comparator.compare(minVal, zero) < 0);
		assertTrue(this.comparator.compare(zero, minVal) > 0);
	}

}
