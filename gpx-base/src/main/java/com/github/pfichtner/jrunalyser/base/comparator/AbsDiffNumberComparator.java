package com.github.pfichtner.jrunalyser.base.comparator;

import java.math.BigDecimal;
import java.util.Comparator;

public class AbsDiffNumberComparator implements Comparator<Number> {

	// TODO negative maxDiffs!?
	private final BigDecimal maxDiff;

	public AbsDiffNumberComparator(int maxDiff) {
		this(new BigDecimal(maxDiff));
	}

	public AbsDiffNumberComparator(BigDecimal maxDiff) {
		this.maxDiff = maxDiff;
	}

	@Override
	public int compare(Number n1, Number n2) {
		BigDecimal bd1 = new BigDecimal(String.valueOf(n1));
		BigDecimal bd2 = new BigDecimal(String.valueOf(n2));
		return bd1.subtract(bd2).abs().compareTo(this.maxDiff) <= 0 ? 0 : bd1
				.compareTo(bd2);
	}

	@Override
	public String toString() {
		return "AbsDiffNumberComparator [maxDiff=" + this.maxDiff + "]";
	}

}
