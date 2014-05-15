package com.github.pfichtner.jrunalyser.base.data.track;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.util.Validator;
import com.google.common.collect.Iterables;

public class TracksTest {

	@Test
	public void testSimpleReverse() throws IOException {
		Track track = toDefault(loadTrack());
		Track rev1 = Tracks.reverse(track);
		Track rev2 = Tracks.reverse(rev1);
		assertEquals(track, rev2);
	}

	@Test
	public void testSegmentedReverse() throws IOException {
		Track track = toDefault(loadTrack());
		Track rev1 = Tracks.reverse(track);
		Track rev2 = Tracks.reverse(rev1);
		assertEquals(track, rev2);
	}

	@Test
	public void testIsAwayEqReturn() throws IOException {
		assertFalse(Tracks.isAwayEqReturn(toDefault(loadTrack())));
	}

	@Test
	@Ignore
	public void testOveralls() throws IOException {
		Track track = toDefault(loadTrack());
		List<? extends LinkedTrackPoint> tps = track.getTrackpoints();

		LinkedTrackPoint first = Iterables.getFirst(tps, null);
		assertEquals(0, first.getOverallDistance()
				.getValue(DistanceUnit.METERS), 0.0);
		assertEquals(0,
				first.getOverallDuration().getValue(TimeUnit.MILLISECONDS), 0.0);

		LinkedTrackPoint last = Iterables.getLast(tps);
		assertEquals(
				track.getStatistics().getDistance()
						.convertTo(DistanceUnit.METERS), last
						.getOverallDistance().convertTo(DistanceUnit.METERS));
		assertEquals(
				track.getStatistics().getDuration().convertTo(TimeUnit.SECONDS),
				last.getOverallDuration().convertTo(TimeUnit.SECONDS));
	}

	private Track toDefault(Track track) {
		return new DefaultTrack(null, track.getMetadata(),
				track.getWaypoints(), track.getSegments(),
				DefaultStatistics.ofTrack(track));
	}

	private Track loadTrack() throws IOException {
		InputStream stream = getClass().getResourceAsStream(
				"/Portland-Ape Cave.gpx");
		try {
			return Validator.validate(GpxUnmarshaller.loadTrack(stream));
		} finally {
			stream.close();
		}
	}

}
