package com.github.pfichtner.jrunalyser.base.data.floater;

import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.DefaultSegment;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;

public class DefaultHighlightableSegment extends DefaultSegment implements
		HighlightableSegment {

	private final boolean highlighted;

	public DefaultHighlightableSegment(List<? extends LinkedTrackPoint> wps,
			boolean highlighted, Statistics statistics) {
		super(wps, statistics);
		this.highlighted = highlighted;
	}

	@Override
	public boolean isHighligted() {
		return this.highlighted;
	}

}
