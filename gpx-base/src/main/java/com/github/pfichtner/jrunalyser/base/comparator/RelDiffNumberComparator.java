package com.github.pfichtner.jrunalyser.base.comparator;

import java.math.BigDecimal;
import java.util.Comparator;

public class RelDiffNumberComparator implements Comparator<Number> {

	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	private final BigDecimal maxDiff;

	public RelDiffNumberComparator(int i) {
		this(new BigDecimal(i));
	}

	public RelDiffNumberComparator(BigDecimal maxDiff) {
		this.maxDiff = maxDiff;
	}

	@Override
	public int compare(Number o1, Number o2) {

		BigDecimal diff = new BigDecimal(String.valueOf(o1))
				.subtract(new BigDecimal(String.valueOf(o2)));
		if (diff.signum() == 0) {
			return 0;
		}

		BigDecimal bd1 = new BigDecimal(String.valueOf(o1));
		BigDecimal bd2 = new BigDecimal(String.valueOf(o2));

		BigDecimal greater = bd1.abs().compareTo(bd2.abs()) > 0 ? bd1 : bd2;

		if (greater.signum() != 0) {
			BigDecimal relDiff = diff.multiply(ONE_HUNDRED).divide(greater, 2,
					BigDecimal.ROUND_HALF_UP);
			if (relDiff.abs().compareTo(this.maxDiff) <= 0)
				return 0;
		}
		return bd1.compareTo(bd2);
	}

	@Override
	public String toString() {
		return "RelDiffNumberComparator [maxDiff=" + this.maxDiff + "]";
	}

}
