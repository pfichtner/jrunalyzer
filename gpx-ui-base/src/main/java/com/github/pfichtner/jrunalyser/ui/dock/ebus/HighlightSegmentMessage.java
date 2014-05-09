package com.github.pfichtner.jrunalyser.ui.dock.ebus;

import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;

/**
 * Message that is sent if a part of the track should be highlighted.
 * 
 * @author Peter Fichtner
 */
@EventBusMessage
public class HighlightSegmentMessage {

	private final SegmentationUnit segmentationUnit;

	public HighlightSegmentMessage(SegmentationUnit segmentationUnit) {
		this.segmentationUnit = segmentationUnit;
	}

	public SegmentationUnit getSegmentationUnit() {
		return this.segmentationUnit;
	}

}
