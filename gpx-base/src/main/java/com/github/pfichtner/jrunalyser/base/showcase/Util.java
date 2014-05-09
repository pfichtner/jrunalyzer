package com.github.pfichtner.jrunalyser.base.showcase;

import java.util.Comparator;
import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.Coordinate;
import com.github.pfichtner.jrunalyser.base.data.GeoUtil;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.TrackComparators.FBC;
import com.github.pfichtner.jrunalyser.base.util.format.LatLonFormatter;

public final class Util {

	private Util() {
		super();
	}

	public static void dumpComparators(final Track ta, final Track tb,
			List<Comparator<Track>> comparators) {
		for (Comparator<Track> comparator : comparators) {
			dumpComparator(ta, tb, comparator);
		}
	}

	public static void dumpComparator(final Track ta, final Track tb,
			Comparator<Track> comparator) {
		String add = "";
		if (comparator instanceof FBC) {
			FBC<?> fbc = ((FBC<?>) comparator);
			Object oa = fbc.applyA(ta);
			Object ob = fbc.applyB(tb);
			if (oa instanceof Coordinate && ob instanceof Coordinate) {
				Coordinate ca = (Coordinate) oa;
				Coordinate cb = (Coordinate) ob;
				add = " (" + LatLonFormatter.instance.format(ca) + " vs. "
						+ LatLonFormatter.instance.format(cb) + " (diff: "
						+ GeoUtil.calcDistance(ca, cb) + "))";
			} else {
				add = " (" + oa + " vs. " + ob + ")";
			}

		}
		System.out
				.println(comparator + ": " + comparator.compare(ta, tb) + add);
	}

}
