package com.github.pfichtner.jrunalyser.ui.dock.ebus;

import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;

@EventBusMessage
public class SegmentationSelected {

	private final SegmentationUnit segmentationUnit;

	public SegmentationSelected(SegmentationUnit segmentationUnit) {
		this.segmentationUnit = segmentationUnit;
	}

	public SegmentationUnit getSegmentationUnit() {
		return this.segmentationUnit;
	}

}
