package com.github.pfichtner.jrunalyser.base.data.segmenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenter;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenters;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.util.Validator;
import com.google.common.io.Closeables;

public class SegmenterTest {

	@Test
	public void testSegmentUnsegment() throws IOException {
		Track track = loadTrack();
		assertTrackSize(track);

		for (int i = 1; i < 90; i += 2) {
			track = Segmenters
					.duration(DefaultDuration.of(i, TimeUnit.MINUTES)).segment(
							track);
			assertFalse(String.valueOf(i + " minutes = "
					+ track.getTrackpoints().size() + " waypoints ("
					+ track.getSegments().size() + " segments)"), track
					.getSegments().size() == 1);
			track = Segmenter.NULL_SEGMENTER.segment(track);
			assertTrackSize(track);
		}
	}

	private void assertTrackSize(Track track) {
		assertEquals(4038, track.getTrackpoints().size());
	}

	private Track loadTrack() throws IOException {
		InputStream stream = getClass().getResourceAsStream(
				"/Portland-Ape Cave.gpx");
		try {
			return Validator.validate(GpxUnmarshaller.loadTrack(stream));
		} finally {
			Closeables.closeQuietly(stream);
		}
	}

}
