package com.github.pfichtner.jrunalyser.base.data;

public class DivideTrack implements SegmentationUnit {

	private final int parts;
	private final Class<? extends SegmentationUnit> basedOn;

	public DivideTrack(int parts, Class<? extends SegmentationUnit> basedOn) {
		this.parts = parts;
		this.basedOn = basedOn;
	}

	public int getParts() {
		return this.parts;
	}

	public Class<? extends SegmentationUnit> getBasedOn() {
		return this.basedOn;
	}

}
