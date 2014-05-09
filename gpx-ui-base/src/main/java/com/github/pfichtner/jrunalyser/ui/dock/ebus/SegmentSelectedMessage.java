package com.github.pfichtner.jrunalyser.ui.dock.ebus;

import com.github.pfichtner.jrunalyser.base.data.segment.Segment;

/**
 * Message that is sent if a previously segmented part of the track (a segment)
 * should be highlighted, e.g. on a mouse over event in the LapInfoTable.
 * 
 * @author Peter Fichtner
 */
@EventBusMessage
public class SegmentSelectedMessage {

	private final int index;
	private final Segment segment;

	public SegmentSelectedMessage(int index, Segment segment) {
		this.index = index;
		this.segment = segment;
	}

	public int getIndex() {
		return this.index;
	}

	public Segment getSegment() {
		return this.segment;
	}

}
