package com.github.pfichtner.jrunalyser.base.showcase;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxMarshaller;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.segment.DefaultSegment;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Metadata;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.Tracks;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.TrackComparators;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.TrackComparators.MultiWaypointDistanceComparator;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * This showcase extracts the tracks' segment startpoints and writes a track
 * containing those waypoints to stdout.
 * 
 * @author Peter Fichtner
 */
public class TrackCompareShowcase2 {

	public static void main(String[] args) throws IOException {

		String dir = "/home/xck10h6/gpx/";
		Track a = loadTrack(new File(dir, "21_05_2012 17_29.gpx"));
		Track b = loadTrack(new File(dir, "30_11_2012 16_55.gpx"));

		System.out.println("Comparing " + getDate(a) + " to " + getDate(b));
		for (Comparator<Track> comparator : TrackComparators.baseAttributes
				.getComparators()) {
			eval(a, b, comparator);
		}

		MultiWaypointDistanceComparator ct = TrackComparators
				.segmentStartPointsEqual(a, b,
						DefaultDistance.of(150, DistanceUnit.METERS));
		Util.dumpComparator(a, b, ct);

		// TODO sysout distance used by Segmenter

		List<WayPoint> waypoints1 = Lists.newArrayList(ct.applyA(a));
		List<WayPoint> waypoints2 = Lists.newArrayList(ct.applyA(b));
		Iterable<WayPoint> wps = Iterables.concat(waypoints1, waypoints2);

		System.out.println("***sizeA: " + waypoints1.size() + " vs. sizeB: "
				+ waypoints2.size());
		System.out.println("***diffs:  "
				+ Joiner.on("\n").join(ct.getDiffs(a, b)));

		// List<Collection<WayPoint>> distributed = Distributor.distribute(
		// Lists.newArrayList(concat), 2);
		// Iterable<WayPoint> wps = Iterables.concat(distributed.get(0),
		// distributed.get(1));

		Track track = new DefaultTrack(null, createMetadataStub(),
				Collections.<WayPoint> emptyList(), Collections.singletonList(new DefaultSegment(GpxUnmarshaller
								.toLinked(wps), null)),
				null);
		GpxMarshaller.writeTrack(new PrintWriter(System.out), track);
	}

	private static Metadata createMetadataStub() {
		return new Metadata() {

			@Override
			public Long getTime() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double getMinLongitude() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public double getMinLatitude() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public double getMaxLongitude() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public double getMaxLatitude() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getDescription() {
				return "desc";
			}
		};
	}

	private static void eval(Track a, Track b,
			Comparator<Track> segmentStartPointsEqual) {
		Util.dumpComparator(a, b, segmentStartPointsEqual);
	}

	private static Date getDate(Track track) {
		return new Date(Tracks.getStartPoint(track).getTime().longValue());
	}

	private static Track loadTrack(File file) throws IOException {
		Track track = GpxUnmarshaller.loadTrack(file);
		return new DefaultTrack(track.getId(), track.getMetadata(),
				track.getWaypoints(), track.getSegments(),
				DefaultStatistics.ofTrack(track));
	}

}
