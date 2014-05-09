package com.github.pfichtner.jrunalyser.base.showcase;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxMarshaller;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.segment.DefaultSegment;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Metadata;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.TrackComparators;
import com.google.common.collect.Lists;

public class MoveToZeroZeroShowcase {

	public static void main(String[] args) throws IOException {
		String dir = "/home/xck10h6/gpx/";
		Track track = loadTrack(new File(dir, "07_04_2013 16_02.gpx"));
		Track result = new DefaultTrack(
				null,
				createMetadataStub(),
				track.getWaypoints(), Collections.singletonList(new DefaultSegment(
												GpxUnmarshaller.toLinked(Lists
														.newArrayList(TrackComparators
																.bearingZeroZeroDecorater(
																		com.github.pfichtner.jrunalyser.base.data.stat.Functions.Tracks.trackpoints)
																.apply(track))), null)),
				null);
		GpxMarshaller.writeTrack(new PrintWriter(System.out), result);
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

	private static Track loadTrack(File file) throws IOException {
		Track track = GpxUnmarshaller.loadTrack(file);
		return new DefaultTrack(track.getId(), track.getMetadata(),
				track.getWaypoints(), track.getSegments(),
				DefaultStatistics.ofTrack(track));
	}

}
