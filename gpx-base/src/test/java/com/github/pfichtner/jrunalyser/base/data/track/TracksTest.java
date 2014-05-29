package com.github.pfichtner.jrunalyser.base.data.track;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.util.Validator;

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

	private Track toDefault(Track track) {
		return new DefaultTrack(null, track.getMetadata(),
				GpxUnmarshaller.toLinked(track.getWaypoints()),
				track.getSegments(), DefaultStatistics.ofTrack(track));
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
