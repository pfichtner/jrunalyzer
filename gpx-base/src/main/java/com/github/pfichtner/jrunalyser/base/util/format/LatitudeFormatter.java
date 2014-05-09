package com.github.pfichtner.jrunalyser.base.util.format;

import java.text.DecimalFormat;

public class LatitudeFormatter {

	private final String n;
	private final String s;

	private final DecimalFormat wholeFormatter = new DecimalFormat("00.###");
	private final DecimalFormat fractionFormatter = new DecimalFormat("##.###");

	public LatitudeFormatter() {
		this("N", "S");
	}

	public LatitudeFormatter(String n, String s) {
		this.n = n;
		this.s = s;
	}

	public String format(double lat) {
		int whole = (int) lat;
		double remainder = lat - whole;
		return (whole > 0 ? this.n : this.s)
				+ this.wholeFormatter.format(Math.abs(whole)) + "° "
				+ this.fractionFormatter.format(Math.abs(remainder * 60));
	}

}
