package com.github.pfichtner.jrunalyser.base.data.segment;

import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.google.common.collect.ImmutableList;

public class DefaultSegment implements Segment {

	private final List<LinkedTrackPoint> wayPoints;
	private final Statistics statistics;

	public DefaultSegment(List<? extends LinkedTrackPoint> wps,
			Statistics statistics) {
		this.wayPoints = ImmutableList.copyOf(wps);
		this.statistics = statistics;
	}

	public List<LinkedTrackPoint> getTrackpoints() {
		return this.wayPoints;
	}

	@Override
	public Statistics getStatistics() {
		return this.statistics;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.statistics == null) ? 0 : this.statistics.hashCode());
		result = prime * result
				+ ((this.wayPoints == null) ? 0 : this.wayPoints.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultSegment other = (DefaultSegment) obj;
		if (this.statistics == null) {
			if (other.statistics != null)
				return false;
		} else if (!this.statistics.equals(other.statistics))
			return false;
		if (this.wayPoints == null) {
			if (other.wayPoints != null)
				return false;
		} else if (!this.wayPoints.equals(other.wayPoints))
			return false;
		return true;
	}

}
