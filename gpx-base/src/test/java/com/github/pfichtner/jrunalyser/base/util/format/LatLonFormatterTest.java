package com.github.pfichtner.jrunalyser.base.util.format;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.util.format.LatLonFormatter;

public class LatLonFormatterTest {

	@Ignore
	// ignored since Locale dependent
	@Test
	public void testSearsTowerChicago() {
		assertEquals("N41° 52,736 W087° 38,185",
				LatLonFormatter.instance.format(41.878928, -87.636417));
	}

}
