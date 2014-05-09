package com.github.pfichtner.jrunalyser.base.data.segmenter;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.DefaultSpeed;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenter;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenters;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.stat.Predicates;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.util.Validator;
import com.google.common.io.Closeables;

public class MathObjectSegmenterTest {

	@Test
	public void testTrack() throws IOException {
		Track track = loadTrack();
		List<? extends LinkedTrackPoint> trkpts = track.getTrackpoints();
		assertEquals(4038, trkpts.size());
		Statistics statistics = DefaultStatistics.ofTrack(track);
		assertEquals(DefaultDistance.of(59.34645953464422, DistanceUnit.MILES),
				statistics.getDistance().convertTo(DistanceUnit.MILES));
		assertEquals(DefaultDuration.of(1.6111111111111112, TimeUnit.HOURS),
				statistics.getDuration().convertTo(TimeUnit.HOURS));
		assertEquals(new DefaultSpeed(59.281385040920696,
				DistanceUnit.KILOMETERS, TimeUnit.HOURS), statistics
				.getAvgSpeed().convert(DistanceUnit.KILOMETERS, TimeUnit.HOURS));
	}

	@Test
	public void testSegmenter400m() throws IOException {
		checkSegment(loadTrack(), Segmenters.floatingDistance(DefaultDistance
				.of(400, DistanceUnit.METERS)), DefaultDistance.of(
				429.26199221834923, DistanceUnit.METERS), DefaultDuration.of(
				0.2, TimeUnit.MINUTES));

	}

	@Test
	public void testSegmenter1000m() throws IOException {
		checkSegment(loadTrack(), Segmenters.floatingDistance(DefaultDistance
				.of(1000, DistanceUnit.METERS)), DefaultDistance.of(
				1013.0587158725085, DistanceUnit.METERS), DefaultDuration.of(
				0.48333333333333334, TimeUnit.MINUTES));

	}

	@Test
	public void testSegmenter1mil() throws IOException {
		checkSegment(loadTrack(), Segmenters.floatingDistance(DefaultDistance
				.of(1, DistanceUnit.MILES)), DefaultDistance.of(
				1.0179844092081238, DistanceUnit.MILES), DefaultDuration.of(
				0.8, TimeUnit.MINUTES));
	}

	@Test
	public void testSegmenter2000m() throws IOException {
		checkSegment(loadTrack(), Segmenters.floatingDistance(DefaultDistance
				.of(1, DistanceUnit.KILOMETERS)), DefaultDistance.of(
				0.6294853073404165, DistanceUnit.MILES), DefaultDuration.of(
				0.48333333333333334, TimeUnit.MINUTES));
	}

	@Test
	public void testSegmenter2mil() throws IOException {
		checkSegment(loadTrack(), Segmenters.floatingDistance(DefaultDistance
				.of(2, DistanceUnit.MILES)), DefaultDistance.of(
				2.0189852513675004, DistanceUnit.MILES), DefaultDuration.of(
				1.6, TimeUnit.MINUTES));
	}

	@Test
	public void testSegmenter15mil() throws IOException {
		checkSegment(loadTrack(), Segmenters.floatingDistance(DefaultDistance
				.of(15, DistanceUnit.MILES)), DefaultDistance.of(
				15.00010301807143, DistanceUnit.MILES), DefaultDuration.of(
				15.316666666666666, TimeUnit.MINUTES));
	}

	// -------------------------------------------------------------------------

	@Test
	public void testSegmenter1000mil() throws IOException {
		assertEquals(
				1,
				Segmenters
						.floatingDistance(
								DefaultDistance.of(1000, DistanceUnit.MILES))
						.segment(loadTrack()).getSegments().size());
	}

	private void checkSegment(Track track, Segmenter segmenter,
			Distance distance, Duration duration) {
		Track segmented = segmenter.segment(track);
		List<Segment> segments = segmented.getSegments();
		assertEquals(3, segments.size());

		Statistics statistics = getOnlyElement(
				filter(segments, Predicates.Segments.isHighligted))
				.getStatistics();
		assertEquals(distance,
				statistics.getDistance().convertTo(distance.getDistanceUnit()));
		assertEquals(duration,
				statistics.getDuration().convertTo(duration.getTimeUnit()));
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
