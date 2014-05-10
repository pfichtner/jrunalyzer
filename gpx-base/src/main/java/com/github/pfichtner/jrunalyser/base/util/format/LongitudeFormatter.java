package com.github.pfichtner.jrunalyser.base.util.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class LongitudeFormatter {

	private final String w;
	private final String e;

	private final DecimalFormat wholeFormatter;
	private final DecimalFormat fractionFormatter;

	public LongitudeFormatter() {
		this(Locale.US);
	}

	public LongitudeFormatter(Locale locale) {
		this(new DecimalFormatSymbols(locale));
	}

	public LongitudeFormatter(DecimalFormatSymbols decimalFormatSymbols) {
		this(decimalFormatSymbols, "W", "E");
	}

	public LongitudeFormatter(String w, String e) {
		this(Locale.US, w, e);
	}

	public LongitudeFormatter(Locale locale, String w, String e) {
		this(new DecimalFormatSymbols(locale), w, e);
	}

	public LongitudeFormatter(DecimalFormatSymbols decimalFormatSymbols,
			String w, String e) {
		this.w = w;
		this.e = e;
		this.wholeFormatter = new DecimalFormat("000.###", decimalFormatSymbols);
		this.fractionFormatter = new DecimalFormat("##.###",
				decimalFormatSymbols);
	}

	public String format(double lng) {
		int whole = (int) lng;
		double remainder = lng - whole;
		return (whole > 0 ? this.e : this.w)
				+ this.wholeFormatter.format(Math.abs(whole)) + "° "
				+ this.fractionFormatter.format(Math.abs(remainder * 60));
	}

}
