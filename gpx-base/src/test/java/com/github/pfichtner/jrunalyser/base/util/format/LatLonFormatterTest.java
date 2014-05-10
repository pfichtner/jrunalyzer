package com.github.pfichtner.jrunalyser.base.util.format;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

public class LatLonFormatterTest {

	@Test
	public void testSearsTowerChicago_us() {
		LatLonFormatter latLonFormatter = new LatLonFormatter(Locale.US);
		assertEquals("N41° 52.736 W087° 38.185",
				formatSearsTowerChicago(latLonFormatter));
	}

	@Test
	public void testSearsTowerChicago_de() {
		LatLonFormatter latLonFormatter = new LatLonFormatter(Locale.GERMANY);
		assertEquals("N41° 52,736 W087° 38,185",
				formatSearsTowerChicago(latLonFormatter));
	}

	public String formatSearsTowerChicago(LatLonFormatter formatter) {
		return formatter.format(41.878928, -87.636417);
	}

}
