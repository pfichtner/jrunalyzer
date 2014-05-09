package com.github.pfichtner.jrunalyser.base.data.track;

import java.util.Collections;
import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DefaultTrack implements Track {

	private final Id id;
	private final Metadata metadata;
	private final List<LinkedTrackPoint> trackpoints;
	private final Statistics statistics;
	private final List<Segment> segments;
	private final List<? extends WayPoint> waypoints;

	public DefaultTrack(Id id, Metadata metadata,
			List<? extends WayPoint> wayPoints, List<? extends Segment> segments,
			Statistics statistics) {
		this.id = id;
		this.metadata = metadata;
		this.statistics = statistics;
		this.segments = ImmutableList.copyOf(segments);
		this.trackpoints = Collections.unmodifiableList(flatter(segments));
		this.waypoints = wayPoints;
	}

	@Override
	public Id getId() {
		return this.id;
	}

	@Override
	public Metadata getMetadata() {
		return this.metadata;
	}

	public List<LinkedTrackPoint> getTrackpoints() {
		return this.trackpoints;
	}

	@Override
	public Statistics getStatistics() {
		return this.statistics;
	}

	private static List<? extends LinkedTrackPoint> flatter(
			List<? extends Segment> segments) {
		List<LinkedTrackPoint> result = Lists.newArrayList();
		for (Segment segment : segments) {
			result.addAll(segment.getTrackpoints());
		}
		return result;
	}

	public List<Segment> getSegments() {
		return this.segments;
	}

	public List<? extends WayPoint> getWaypoints() {
		return this.waypoints;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result
				+ ((this.metadata == null) ? 0 : this.metadata.hashCode());
		result = prime * result
				+ ((this.segments == null) ? 0 : this.segments.hashCode());
		result = prime * result
				+ ((this.statistics == null) ? 0 : this.statistics.hashCode());
		result = prime
				* result
				+ ((this.trackpoints == null) ? 0 : this.trackpoints.hashCode());
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
		DefaultTrack other = (DefaultTrack) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		if (this.metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!this.metadata.equals(other.metadata))
			return false;
		if (this.segments == null) {
			if (other.segments != null)
				return false;
		} else if (!this.segments.equals(other.segments))
			return false;
		if (this.statistics == null) {
			if (other.statistics != null)
				return false;
		} else if (!this.statistics.equals(other.statistics))
			return false;
		if (this.trackpoints == null) {
			if (other.trackpoints != null)
				return false;
		} else if (!this.trackpoints.equals(other.trackpoints))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DefaultTrack [id()=" + getId() + "]";
	}

}
