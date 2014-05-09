package com.github.pfichtner.jrunalyser.base.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Date;
import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.track.Track;

public final class Validator {

	private Validator() {
		super();
	}

	public static Track validate(Track trackArg) {
		Track track = checkNotNull(trackArg);
		validateSegments(track.getSegments());
		validateWaypoints(track.getTrackpoints());
		return trackArg;
	}

	public static List<Segment> validateSegments(List<Segment> segments) {
		for (Segment segment : checkNotNull(segments)) {
			validateWaypoints(segment.getTrackpoints());
		}
		return segments;
	}

	public static List<? extends LinkedTrackPoint> validateWaypoints(
			List<? extends LinkedTrackPoint> wps) {
		checkState(!checkNotNull(wps).isEmpty());
		LinkedTrackPoint old = null;
		for (LinkedTrackPoint wp : wps) {
			if (old != null) {
				checkState(wp.getTime().longValue() >= old.getTime()
						.longValue(), "%s < %s (%s %s", new Date(wp.getTime()
						.longValue()), new Date(old.getTime().longValue()),
						old, wp);
			}
			old = wp;
		}
		return wps;
	}

}
