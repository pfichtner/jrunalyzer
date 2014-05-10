package com.github.pfichtner.jrunalyser.base.util.format;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.github.pfichtner.jrunalyser.base.data.Coordinate;

public class LatLonFormatter {

	// http://www.cachewiki.de/wiki/Koordinaten

	public static final LatLonFormatter instance = new LatLonFormatter();

	private final LatitudeFormatter latitudeFormatter;
	private final LongitudeFormatter longitudeFormatter;

	public LatLonFormatter() {
		this(Locale.getDefault());
	}

	public LatLonFormatter(Locale locale) {
		this(new LatitudeFormatter(new DecimalFormatSymbols(locale)),
				new LongitudeFormatter(new DecimalFormatSymbols(locale)));
	}

	public LatLonFormatter(LatitudeFormatter latitudeFormatter,
			LongitudeFormatter longitudeFormatter) {
		this.latitudeFormatter = latitudeFormatter;
		this.longitudeFormatter = longitudeFormatter;
	}

	public String format(Coordinate coordinate) {
		return format(coordinate.getLatitude(), coordinate.getLongitude());
	}

	public String format(double lat, double lng) {
		return this.latitudeFormatter.format(lat) + ' '
				+ this.longitudeFormatter.format(lng);
	}

}
