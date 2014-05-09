package com.github.pfichtner.jrunalyser.base.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.util.MovingAverageIterator;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

public class MovingAverageIteratorTest {

	@Test
	public void testX() {
		List<Double> in = Doubles.asList(1, 2, 2, 3, 9, 1, 2);
		check(1, in, in);
		check(2, Doubles.asList(1, 1.5, 2, 2.5, 6, 5, 1.5), in);
		check(3, Doubles.asList(1, 1.5, 1.6666666666666667, 2.3333333333333335,
				4.666666666666667, 4.333333333333333, 4), in);
		check(4,
				Doubles.asList(1.0, 1.5, 1.6666666666666667, 2, 4, 3.75, 3.75),
				in);
		check(5, Doubles.asList(1.0, 1.5, 1.6666666666666667, 2.0, 3.4, 3.4,
				3.4), in);
		check(6, Doubles.asList(1.0, 1.5, 1.6666666666666667, 2.0, 3.4, 3.0,
				3.1666666666666665), in);
		check(7, Doubles.asList(1.0, 1.5, 1.6666666666666667, 2.0, 3.4, 3.0,
				2.857142857142857), in);

		for (int i = 8; i < 20; i++) {
			check(i, Doubles.asList(1.0, 1.5, 1.6666666666666667, 2.0, 3.4,
					3.0, 2.857142857142857), in);
		}
	}

	private static void check(int period, List<Double> expected,
			List<Double> values) {
		assertEquals(expected, Lists.newArrayList(new MovingAverageIterator(
				values.iterator(), period)));
	}

}
