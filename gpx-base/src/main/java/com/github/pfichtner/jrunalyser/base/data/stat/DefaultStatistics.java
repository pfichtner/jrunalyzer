package com.github.pfichtner.jrunalyser.base.data.stat;

import static com.github.pfichtner.jrunalyser.base.data.stat.Predicates.LinkedWayPoints.hasLink;
import static com.github.pfichtner.jrunalyser.base.util.Validator.validateWaypoints;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DefaultStatistics implements Statistics, Serializable {

	private static final long serialVersionUID = 5608387056728759224L;

	private final WayPoint minElevation;
	private final WayPoint maxElevation;
	private final int ascent;
	private final int descent;
	private final LinkedTrackPoint minSpeed;
	private final LinkedTrackPoint maxSpeed;
	private final Speed avgSpeed;
	private final Distance distance;
	private final Duration duration;

	private DefaultStatistics(Statistics stats) {
		this.minElevation = stats.getMinElevation();
		this.maxElevation = stats.getMaxElevation();
		this.ascent = stats.getAscent();
		this.descent = stats.getDescent();
		this.minSpeed = stats.getMinSpeed();
		this.maxSpeed = stats.getMaxSpeed();
		this.avgSpeed = stats.getAvgSpeed();
		this.distance = stats.getDistance();
		this.duration = stats.getDuration();
	}

	public static Statistics copyOf(Statistics stats) {
		return stats == null ? NULL : new DefaultStatistics(stats);
	}

	public static Statistics ofTrack(Track track) {
		List<Statistics> statistics = Lists.newArrayList();
		for (Segment segment : track.getSegments()) {
			statistics.add(ofSegment(segment));
		}
		return DefaultStatistics.copyOf(CombinedStatistics.of(statistics));
	}

	public static Statistics ofSegment(Segment segment) {
		return new DefaultStatistics(segment.getTrackpoints());
	}

	public static Statistics ofWaypoints(List<? extends LinkedTrackPoint> wps) {
		return new DefaultStatistics(wps);
	}

	private DefaultStatistics(List<? extends LinkedTrackPoint> wps) {
		List<? extends LinkedTrackPoint> wayPoints = checkNotNull(wps);
		checkArgument(!wps.isEmpty(), "Waypoints are empty");
		assert validateWaypoints(wayPoints) != null;

		Iterable<? extends LinkedTrackPoint> linkedTps = filter(wayPoints,
				hasLink());

		this.minElevation = StatCalculators.minEle().calculate(wayPoints);
		this.maxElevation = StatCalculators.maxEle().calculate(wayPoints);

		this.ascent = StatCalculators.ascent().calculate(linkedTps).intValue();
		this.descent = StatCalculators.descent().calculate(linkedTps)
				.intValue();
		// ----------------------------------------------------------

		this.minSpeed = StatCalculators.minSpeed().calculate(linkedTps);
		this.maxSpeed = StatCalculators.maxSpeed().calculate(linkedTps);
		this.avgSpeed = StatCalculators.avgSpeed(DistanceUnit.METERS,
				TimeUnit.SECONDS).calculate(linkedTps);

		this.distance = StatCalculators.distance(DistanceUnit.METERS)
				.calculate(linkedTps);

		// ----------------------------------------------------------

		this.duration = wayPoints.isEmpty() ? null : calcDuration(wayPoints);
	}

	private static Duration calcDuration(
			List<? extends LinkedTrackPoint> wayPoints) {
		long startTime = wayPoints.get(0).getTime().longValue();
		LinkedTrackPoint last = Iterables.getLast(wayPoints);
		long endTime = last.getLink() == null ? last.getTime().longValue()
				: last.getLink().getNext().getTime().longValue();
		return DefaultDuration.of(endTime - startTime, TimeUnit.MILLISECONDS)
				.convertTo(TimeUnit.SECONDS);
	}

	public Distance getDistance() {
		return this.distance;
	}

	public Duration getDuration() {
		return this.duration;
	}

	public WayPoint getMinElevation() {
		return this.minElevation;
	}

	public WayPoint getMaxElevation() {
		return this.maxElevation;
	}

	public int getAscent() {
		return this.ascent;
	}

	public int getDescent() {
		return this.descent;
	}

	public LinkedTrackPoint getMinSpeed() {
		return this.minSpeed;
	}

	public LinkedTrackPoint getMaxSpeed() {
		return this.maxSpeed;
	}

	@Override
	public Speed getAvgSpeed() {
		return this.avgSpeed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.ascent;
		result = prime * result
				+ ((this.avgSpeed == null) ? 0 : this.avgSpeed.hashCode());
		result = prime * result + this.descent;
		result = prime * result
				+ ((this.distance == null) ? 0 : this.distance.hashCode());
		result = prime * result
				+ ((this.duration == null) ? 0 : this.duration.hashCode());
		result = prime
				* result
				+ ((this.maxElevation == null) ? 0 : this.maxElevation
						.hashCode());
		result = prime * result
				+ ((this.maxSpeed == null) ? 0 : this.maxSpeed.hashCode());
		result = prime
				* result
				+ ((this.minElevation == null) ? 0 : this.minElevation
						.hashCode());
		result = prime * result
				+ ((this.minSpeed == null) ? 0 : this.minSpeed.hashCode());
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
		DefaultStatistics other = (DefaultStatistics) obj;
		if (this.ascent != other.ascent)
			return false;
		if (this.avgSpeed == null) {
			if (other.avgSpeed != null)
				return false;
		} else if (!this.avgSpeed.equals(other.avgSpeed))
			return false;
		if (this.descent != other.descent)
			return false;
		if (this.distance == null) {
			if (other.distance != null)
				return false;
		} else if (!this.distance.equals(other.distance))
			return false;
		if (this.duration == null) {
			if (other.duration != null)
				return false;
		} else if (!this.duration.equals(other.duration))
			return false;
		if (this.maxElevation == null) {
			if (other.maxElevation != null)
				return false;
		} else if (!this.maxElevation.equals(other.maxElevation))
			return false;
		if (this.maxSpeed == null) {
			if (other.maxSpeed != null)
				return false;
		} else if (!this.maxSpeed.equals(other.maxSpeed))
			return false;
		if (this.minElevation == null) {
			if (other.minElevation != null)
				return false;
		} else if (!this.minElevation.equals(other.minElevation))
			return false;
		if (this.minSpeed == null) {
			if (other.minSpeed != null)
				return false;
		} else if (!this.minSpeed.equals(other.minSpeed))
			return false;
		return true;
	}

}
