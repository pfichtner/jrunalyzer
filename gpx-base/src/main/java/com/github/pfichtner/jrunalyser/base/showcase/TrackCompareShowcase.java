package com.github.pfichtner.jrunalyser.base.showcase;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;

import com.github.pfichtner.jrunalyser.base.data.StaticFileProvider;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.TrackComparators;

public class TrackCompareShowcase {

	public static void main(String[] args) throws IOException {

		File[] fixFiles = StaticFileProvider.getFixFiles();

		Comparator<Track> tc = TrackComparators.byAttributes;

		Track a = loadTrack(fixFiles[50]);
		Track b = loadTrack(fixFiles[48]);

		System.out.println("Comparing " + getDate(a) + " to " + getDate(b));
		sysout(tc, a, b);

		System.out.println();
		System.out.println("Comparing to " + getDate(a));

		int cnt = 0;
		for (File file : fixFiles) {
			System.out.print((++cnt) + "# ");
			sysout(tc, a, loadTrack(file));
		}

	}

	private static void sysout(Comparator<Track> tc, Track a, Track b) {
		System.out.println(getDate(b) + ": " + (tc.compare(a, b) == 0));
	}

	private static Date getDate(Track track) {
		return new Date(track.getTrackpoints().get(0).getTime().longValue());
	}

	private static Track loadTrack(File file) throws IOException {
		return GpxUnmarshaller.loadTrack(file);
	}

}
