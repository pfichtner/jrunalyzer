package com.github.pfichtner.jrunalyser.base.util.format;

import java.text.DecimalFormat;

public class LongitudeFormatter {

	private final String w;
	private final String e;

	private final DecimalFormat wholeFormatter = new DecimalFormat("000.###");
	private final DecimalFormat fractionFormatter = new DecimalFormat("##.###");

	public LongitudeFormatter() {
		this("W", "E");
	}

	public LongitudeFormatter(String w, String e) {
		this.w = w;
		this.e = e;
	}

	public String format(double lng) {
		int whole = (int) lng;
		double remainder = lng - whole;
		return (whole > 0 ? this.e : this.w)
				+ this.wholeFormatter.format(Math.abs(whole)) + "° "
				+ this.fractionFormatter.format(Math.abs(remainder * 60));
	}

}
