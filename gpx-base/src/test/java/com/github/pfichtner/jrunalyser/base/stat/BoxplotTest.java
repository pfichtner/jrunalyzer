package com.github.pfichtner.jrunalyser.base.stat;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BoxplotTest {

	private final Boxplot boxplot = new Boxplot(new double[] { 71, 70, 73, 70,
			70, 69, 70, 72, 71, 300, 71, 69 });

	@Test
	public void testSort() {
		assertArrayEquals(new double[] { 69, 69, 70, 70, 70, 70, 71, 71, 71,
				72, 73, 300 }, this.boxplot.getValues(), 0.0);
	}

	@Test
	public void testMedianOddEven() {
		assertEquals(Double.valueOf(2),
				Double.valueOf(new Boxplot(new double[] { 1, 3, 2 }).median()));
		assertEquals(Double.valueOf(2.5), Double.valueOf(new Boxplot(
				new double[] { 3, 1, 3, 2 }).median()));
	}

	@Test
	public void testMedianReal() {
		assertEquals(Double.valueOf(70.5),
				Double.valueOf(this.boxplot.median()));
	}

	@Test
	public void testQ1() {
		assertEquals(Double.valueOf(70), Double.valueOf(this.boxplot.q1()));
	}

	@Test
	public void testQ3() {
		assertEquals(Double.valueOf(71.5), Double.valueOf(this.boxplot.q3()));
	}

	@Test
	public void testInnerFences() {
		Boxplot.InterquatileRange interquatileRange = this.boxplot
				.innerFences();
		assertEquals(Double.valueOf(67.75),
				Double.valueOf(interquatileRange.getLower()));
		assertEquals(Double.valueOf(73.75),
				Double.valueOf(interquatileRange.getUpper()));
	}

	@Test
	public void testInnerFencesGetValues() {
		Boxplot.InterquatileRange interquatileRange = this.boxplot
				.innerFences();
		assertArrayEquals(new double[] { 69, 69, 70, 70, 70, 70, 71, 71, 71,
				72, 73, }, this.boxplot.getValues(interquatileRange), 0.0);
	}

	@Test
	public void testOuterFences() {
		Boxplot.InterquatileRange interquatileRange = this.boxplot
				.outerFences();
		assertEquals(Double.valueOf(65.5),
				Double.valueOf(interquatileRange.getLower()));
		assertEquals(Double.valueOf(76),
				Double.valueOf(interquatileRange.getUpper()));
	}

	@Test
	public void testOuterFencesGetValues() {
		Boxplot.InterquatileRange interquatileRange = this.boxplot
				.outerFences();
		assertArrayEquals(new double[] { 69, 69, 70, 70, 70, 70, 71, 71, 71,
				72, 73, }, this.boxplot.getValues(interquatileRange), 0.0);
	}

}
