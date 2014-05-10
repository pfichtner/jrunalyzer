package com.github.pfichtner.jrunalyser.base.util.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class LatitudeFormatter {

	private final String n;
	private final String s;

	private final DecimalFormat wholeFormatter;
	private final DecimalFormat fractionFormatter;

	public LatitudeFormatter() {
		this(Locale.getDefault());
	}

	public LatitudeFormatter(Locale locale) {
		this(new DecimalFormatSymbols(locale));
	}

	public LatitudeFormatter(DecimalFormatSymbols decimalFormatSymbols) {
		this(decimalFormatSymbols, "N", "S");
	}

	public LatitudeFormatter(String n, String s) {
		this(Locale.US, n, s);
	}

	public LatitudeFormatter(Locale locale, String n, String s) {
		this(new DecimalFormatSymbols(locale), n, s);
	}

	public LatitudeFormatter(DecimalFormatSymbols decimalFormatSymbols,
			String n, String s) {
		this.n = n;
		this.s = s;
		this.wholeFormatter = new DecimalFormat("00.###", decimalFormatSymbols);
		this.fractionFormatter = new DecimalFormat("##.###",
				decimalFormatSymbols);
	}

	public String format(double lat) {
		int whole = (int) lat;
		double remainder = lat - whole;
		return (whole > 0 ? this.n : this.s)
				+ this.wholeFormatter.format(Math.abs(whole)) + "° "
				+ this.fractionFormatter.format(Math.abs(remainder * 60));
	}

}
