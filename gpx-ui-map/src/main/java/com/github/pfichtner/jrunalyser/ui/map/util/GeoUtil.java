package com.github.pfichtner.jrunalyser.ui.map.util;

import static com.github.pfichtner.jrunalyser.base.data.stat.Functions.Metadatas.maxLatitude;
import static com.github.pfichtner.jrunalyser.base.data.stat.Functions.Metadatas.maxLongitude;
import static com.github.pfichtner.jrunalyser.base.data.stat.Functions.Metadatas.minLatitude;
import static com.github.pfichtner.jrunalyser.base.data.stat.Functions.Metadatas.minLongitude;
import static com.github.pfichtner.jrunalyser.base.data.stat.Functions.Tracks.metadata;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Metadata;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.stat.Boxplot;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

public final class GeoUtil {

	private GeoUtil() {
		super();
	}

	public enum CenterType {
		AVERAGE {
			@Override
			public double avg(Iterable<Double> doubles) {
				double sum = 0.0;
				int cnt = 0;
				for (Double d : doubles) {
					sum += d.doubleValue();
					cnt++;
				}
				return cnt > 0 ? sum / cnt : 0.0;
			}
		},
		BOXPLOT_AVERAGE {
			@Override
			public double avg(Iterable<Double> doubles) {
				Boxplot boxplot = new Boxplot(Lists.newArrayList(doubles));
				return AVERAGE.avg(Doubles.asList(boxplot.getValues(boxplot
						.outerFences())));
			}
		};

		public abstract double avg(Iterable<Double> transform);
	}

	public static Rectangle2D getBounds(Track track, JXMapViewer jxMapViewer) {
		return getBounds(track, jxMapViewer.getZoom(),
				jxMapViewer.getTileFactory());
	}

	public static Rectangle2D getBounds(Track track, int zoomLevel,
			TileFactory tileFactory) {
		TileFactoryInfo info = tileFactory.getInfo();
		double minLat = track.getMetadata().getMinLatitude();
		double minLng = track.getMetadata().getMinLongitude();
		double maxLat = track.getMetadata().getMaxLatitude();
		double maxLng = track.getMetadata().getMaxLongitude();

		Point2D nw = org.jdesktop.swingx.mapviewer.util.GeoUtil
				.getBitmapCoordinate(minLat, minLng, zoomLevel, info);
		Point2D se = org.jdesktop.swingx.mapviewer.util.GeoUtil
				.getBitmapCoordinate(maxLat, maxLng, zoomLevel, info);

		double mx = Math.min(nw.getX(), se.getX());
		double my = Math.min(nw.getY(), se.getY());
		double mw = Math.max(nw.getX(), se.getX()) - mx;
		double mh = Math.max(nw.getY(), se.getY()) - my;

		return new Rectangle2D.Double(mx, my, mw, mh);
	}

	public static GeoPosition calcCenter(Track track) {
		Metadata md = track.getMetadata();
		double lat1 = md.getMinLatitude();
		double lng1 = md.getMinLongitude();
		double lat2 = md.getMaxLatitude();
		double lng2 = md.getMaxLongitude();
		return calcCenter(lat1, lng1, lat2, lng2);
	}

	public static GeoPosition calcCenter(Iterable<Track> tracks,
			CenterType centerType) {
		FluentIterable<Metadata> metadatas = FluentIterable.from(tracks)
				.transform(metadata);
		double lat1 = centerType.avg(metadatas.transform(minLatitude));
		double lng1 = centerType.avg(metadatas.transform(minLongitude));
		double lat2 = centerType.avg(metadatas.transform(maxLatitude));
		double lng2 = centerType.avg(metadatas.transform(maxLongitude));
		return calcCenter(lat1, lng1, lat2, lng2);
	}

	private static GeoPosition calcCenter(double lat1, double lng1,
			double lat2, double lng2) {
		return new GeoPosition((lat1 + lat2) / 2, (lng1 + lng2) / 2);
	}

	public static int calcMaxZoomLevel(JXMapKit mapKit, Track track) {
		JXMapViewer mapViewer = mapKit.getMainMap();
		int mmw = mapViewer.getWidth();
		int mmh = mapViewer.getHeight();
		TileFactory tileFactory = mapViewer.getTileFactory();
		int maximumZoomLevel = tileFactory.getInfo().getMaximumZoomLevel();
		int minimumZoomLevel = tileFactory.getInfo().getMinimumZoomLevel();
		for (int i = maximumZoomLevel; i >= minimumZoomLevel; i--) {
			Rectangle2D tr = getBounds(track, i, tileFactory);
			if (tr.getWidth() > mmw || tr.getHeight() > mmh) {
				return i + 1;
			}
		}
		return minimumZoomLevel;
	}

	public static GeoPosition toGeoPoint(WayPoint trackPoint) {
		return new GeoPosition(trackPoint.getLatitude(),
				trackPoint.getLongitude());
	}

}
