package com.github.pfichtner.jrunalyser.base.data.track;

import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.segment.Segment;

public interface Track extends TrackpointProvider, WaypointProvider,
		StatisticsProvider {

	Id getId();

	List<Segment> getSegments();

	Metadata getMetadata();

}
