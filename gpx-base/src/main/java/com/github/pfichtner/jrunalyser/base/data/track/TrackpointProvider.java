package com.github.pfichtner.jrunalyser.base.data.track;

import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;

public interface TrackpointProvider {

	List<? extends LinkedTrackPoint> getTrackpoints();

}
