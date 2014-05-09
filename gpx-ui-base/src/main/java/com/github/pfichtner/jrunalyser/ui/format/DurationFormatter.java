package com.github.pfichtner.jrunalyser.ui.format;

import static com.github.pfichtner.durationformatter.DurationFormatter.SuppressZeros.LEADING;
import static com.github.pfichtner.durationformatter.DurationFormatter.SuppressZeros.MIDDLE;
import static com.github.pfichtner.durationformatter.DurationFormatter.SuppressZeros.TRAILING;

import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.Duration;

public class DurationFormatter {

	public enum Type {
		SHORT_SYMBOLS, SHORT, MEDIUM_SYMBOLS;
	}

	private final com.github.pfichtner.durationformatter.DurationFormatter df;

	public DurationFormatter(Type type) {
		switch (type) {
		case SHORT_SYMBOLS:
			this.df = com.github.pfichtner.durationformatter.DurationFormatter.Builder.SYMBOLS
					.suppressZeros(LEADING, TRAILING).build();
			break;
		case MEDIUM_SYMBOLS:
			this.df = com.github.pfichtner.durationformatter.DurationFormatter.Builder.SYMBOLS
					.suppressZeros(LEADING, MIDDLE, TRAILING)
					.maximumAmountOfUnitsToShow(2).build();
			break;
		case SHORT:
			this.df = com.github.pfichtner.durationformatter.DurationFormatter.DIGITS;
			break;
		default:
			throw new IllegalStateException("Unknown case " + type); //$NON-NLS-1$
		}
	}

	public String format(Duration duration) {
		return this.df.formatMillis((long) duration
				.getValue(TimeUnit.MILLISECONDS));
	}

}
