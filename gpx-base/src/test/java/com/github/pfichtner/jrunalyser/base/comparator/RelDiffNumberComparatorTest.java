package com.github.pfichtner.jrunalyser.base.comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.comparator.RelDiffNumberComparator;

public class RelDiffNumberComparatorTest {

	private static final Integer zero = Integer.valueOf(0);

	private static final Integer p100 = Integer.valueOf(100);
	private static final Integer n100 = Integer.valueOf(-100);

	private static final Integer p90 = Integer.valueOf(90);
	private static final Integer n90 = Integer.valueOf(-90);

	private static final Integer p89 = Integer.valueOf(89);
	private static final Integer n89 = Integer.valueOf(-89);

	private static final Integer maxVal = Integer.valueOf(Integer.MAX_VALUE);
	private static final Integer minVal = Integer.valueOf(Integer.MIN_VALUE);

	// values can differ up to 10 percent (relative)
	private final RelDiffNumberComparator comparator = new RelDiffNumberComparator(
			10);

	@Test
	public void testCompare_resultMustBeZero() {
		assertEquals(0, this.comparator.compare(zero, zero));
		assertEquals(0, this.comparator.compare(p100, p90));
		assertEquals(0, this.comparator.compare(p90, p100));
		assertEquals(0, this.comparator.compare(n100, n90));
		assertEquals(0, this.comparator.compare(n90, n100));

		assertEquals(0, this.comparator.compare(p89, p90));
		assertEquals(0, this.comparator.compare(p90, p89));
		assertEquals(0, this.comparator.compare(n89, n90));
		assertEquals(0, this.comparator.compare(n90, n89));
	}

	@Test
	public void testCompare_resultMustBeNonZero() {
		assertTrue(this.comparator.compare(p100, zero) > 0);
		assertTrue(this.comparator.compare(zero, p100) < 0);

		assertTrue(this.comparator.compare(p89, zero) > 0);
		assertTrue(this.comparator.compare(zero, p89) < 0);

		assertTrue(this.comparator.compare(p100, p89) > 0);
		assertTrue(this.comparator.compare(n89, p100) < 0);
	}

}
