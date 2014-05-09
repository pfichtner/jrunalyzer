package com.github.pfichtner.jrunalyser.ui.tracklist;

import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;

public class MinMaxElevation implements Comparable<MinMaxElevation> {

	private final WayPoint minElevation;
	private final WayPoint maxElevation;

	public MinMaxElevation(WayPoint minElevation, WayPoint maxElevation) {
		this.minElevation = minElevation;
		this.maxElevation = maxElevation;
	}

	public WayPoint getMinElevation() {
		return this.minElevation;
	}

	public WayPoint getMaxElevation() {
		return this.maxElevation;
	}

	public int getDiff() {
		return IntMath.checkedSubtract(getMaxElevation().getElevation()
				.intValue(), getMinElevation().getElevation().intValue());
	}

	public int getDiffAbs() {
		return Math.abs(getDiff());
	}

	public String getText() {
		return getMinElevation().getElevation().intValue() + "/" //$NON-NLS-1$
				+ getMaxElevation().getElevation().intValue();
	}

	public String getTextLong() {
		return getText() + " (" + getDiffAbs() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public int compareTo(MinMaxElevation other) {
		return Ints.compare(getDiffAbs(), other.getDiffAbs());
	}

	@Override
	public int hashCode() {
		return getDiff();
	}

	@Override
	public boolean equals(Object obj) {
		MinMaxElevation other = (MinMaxElevation) obj;
		return getDiff() == other.getDiff()
				&& getTextLong().equals(other.getTextLong());
	}

	@Override
	public String toString() {
		return getTextLong();
	}

}
