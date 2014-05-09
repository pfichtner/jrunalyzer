package com.github.pfichtner.jrunalyser.base.data.track;

import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.WayPoint;

public interface WaypointProvider {

	List<? extends WayPoint> getWaypoints();

}
