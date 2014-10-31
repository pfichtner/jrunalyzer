package com.github.pfichtner.jrunalyser.base.showcase;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.Tracks;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.TrackComparators;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.TrackComparators.ChainedComparator;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.TrackComparators.MultiWaypointDistanceComparator;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;

public class TrackCompareShowcase3 {

	private static final DefaultDistance INFINITE = DefaultDistance.of(
			Integer.MAX_VALUE, DistanceUnit.KILOMETERS);

	public static void main(String[] args) throws IOException {

		String dir = "/home/xck10h6/gpx/";

		File[] allFiles = new File(dir).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		for (File fa : allFiles) {
			final Track ta = loadTrack(fa);
			for (File fb : allFiles) {
				if (!fb.equals(fa)) {
					final Track tb = loadTrack(fb);

					ChainedComparator<Track> baseAttributes = TrackComparators.baseAttributes;

					Comparator<Track> segmentStartPointsEqual = TrackComparators
							.segmentStartPointsEqual(ta, tb, DefaultDistance
									.of(150, DistanceUnit.METERS));

					int r = ComparisonChain.start()
							.compare(ta, tb, baseAttributes)
							.compare(ta, tb, segmentStartPointsEqual).result();

					if (r == 0) {
						System.out.println(getDate(ta) + " =*= " + getDate(tb));
					} else {
						List<Comparator<Track>> comparators = baseAttributes
								.getComparators();
						Distance maxDiff = Ordering.natural().max(
								TrackComparators.segmentStartPointsEqual(ta,
										tb, INFINITE).getDiffs(ta, tb));
						boolean diffInRange = Range.<Distance> lessThan(
								DefaultDistance.of(300, DistanceUnit.METERS))
								.apply(maxDiff);
						if (countEq(ta, tb, comparators) > comparators.size() * 3 / 4
								&& diffInRange) {
							System.out.println(getDate(ta) + " vs. "
									+ getDate(tb));
							Util.dumpComparators(ta, tb, comparators);

							System.out.println("maxDiff is " + maxDiff);
							if (diffInRange) {
								dumpDiffAmounts(ta, tb);

								int i = 0;
								for (Iterator<Distance> iterator = TrackComparators
										.segmentStartPointsEqual(ta, tb,
												INFINITE).getDiffs(ta, tb); iterator
										.hasNext();) {
									System.out.println(i++ + " "
											+ iterator.next());
								}
							}

							System.out.println(Strings.repeat("*", 80));
							System.out.println();
						}
					}

				}
			}
		}
	}

	private static void dumpDiffAmounts(final Track ta, final Track tb) {
		Distance[] ds = new Distance[] {
				DefaultDistance.of(300, DistanceUnit.METERS),
				DefaultDistance.of(200, DistanceUnit.METERS),
				DefaultDistance.of(150, DistanceUnit.METERS),
				DefaultDistance.of(100, DistanceUnit.METERS),
				DefaultDistance.of(75, DistanceUnit.METERS),
				DefaultDistance.of(50, DistanceUnit.METERS),
				DefaultDistance.of(25, DistanceUnit.METERS),
				DefaultDistance.of(10, DistanceUnit.METERS) };
		for (Distance distance : ds) {
			MultiWaypointDistanceComparator mwdc = TrackComparators
					.segmentStartPointsEqual(ta, tb, distance);
			Iterator<Distance> wpDistanceDiffIterator = mwdc.getDiffs(ta, tb);
			int count = Iterators.size(Iterators.filter(wpDistanceDiffIterator,
					Range.lessThan(distance)));
			System.out.println("Amount of Waypoints diffs < " + distance + ": "
					+ count + " of " + Iterators.size(mwdc.getDiffs(ta, tb))
					+ " waypoints");
		}
	}

	private static <T> int countEq(T ta, T tb,
			Collection<Comparator<T>> comparators) {
		int eq = 0;
		for (Comparator<T> comparator : comparators) {
			if (comparator.compare(ta, tb) == 0) {
				eq++;
			}
		}
		return eq;
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
