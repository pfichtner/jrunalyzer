package com.github.pfichtner.jrunalyser.base.data.jaxb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.pfichtner.jrunalyser.base.data.DefaultLink;
import com.github.pfichtner.jrunalyser.base.data.DefaultLinkedWayPoint;
import com.github.pfichtner.jrunalyser.base.data.DefaultWayPoint;
import com.github.pfichtner.jrunalyser.base.data.Link;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.DefaultSegment;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Metadata;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.topografix.gpx._1._1.BoundsType;
import com.topografix.gpx._1._1.GpxType;
import com.topografix.gpx._1._1.MetadataType;
import com.topografix.gpx._1._1.TrkType;
import com.topografix.gpx._1._1.TrksegType;
import com.topografix.gpx._1._1.WptType;

public final class GpxUnmarshaller {

	public static class MetadataAdapter implements Metadata {

		private final Optional<MetadataType> metadata;

		private final static MetadataType NULL = createMetadataType();

		private static MetadataType createMetadataType() {
			MetadataType result = new MetadataType();
			BoundsType bounds = new BoundsType();
			BigDecimal zero = BigDecimal.ZERO;
			bounds.setMinlat(zero);
			bounds.setMaxlat(zero);
			bounds.setMinlon(zero);
			bounds.setMaxlon(zero);
			result.setBounds(bounds);
			return result;
		}

		public MetadataAdapter(MetadataType metadata) {
			this.metadata = Optional.fromNullable(metadata);
		}

		@Override
		public String getName() {
			return this.metadata.or(NULL).getName();
		}

		@Override
		public String getDescription() {
			return this.metadata.or(NULL).getDesc();
		}

		@Override
		public Long getTime() {
			XMLGregorianCalendar cal = this.metadata.or(NULL).getTime();
			return cal == null ? null : Long.valueOf(cal.toGregorianCalendar()
					.getTimeInMillis());
		}

		@Override
		public double getMinLatitude() {
			return this.metadata.or(NULL).getBounds().getMinlat().doubleValue();
		}

		@Override
		public double getMinLongitude() {
			return this.metadata.or(NULL).getBounds().getMinlon().doubleValue();
		}

		@Override
		public double getMaxLatitude() {
			return this.metadata.or(NULL).getBounds().getMaxlat().doubleValue();
		}

		@Override
		public double getMaxLongitude() {
			return this.metadata.or(NULL).getBounds().getMaxlon().doubleValue();
		}

	}

	private GpxUnmarshaller() {
		super();
	}

	public static class WayPointAdapter implements WayPoint {

		private final WptType wptType;

		public WayPointAdapter(WptType wptType) {
			this.wptType = wptType;
		}

		public double getLatitude() {
			return this.wptType.getLat().doubleValue();
		}

		public double getLongitude() {
			return this.wptType.getLon().doubleValue();
		}

		public Integer getElevation() {
			BigDecimal ele = this.wptType.getEle();
			return ele == null ? null : Integer.valueOf(ele.intValue());
		}

		public Long getTime() {
			XMLGregorianCalendar cal = this.wptType.getTime();
			return cal == null ? null : Long.valueOf(cal.toGregorianCalendar()
					.getTimeInMillis());
		}

		@Override
		public String getName() {
			return this.wptType.getName();
		}

		@Override
		public String toString() {
			return "TrackPointAdapter [latitude=" + getLatitude()
					+ ", longitude=" + getLongitude() + ", elevation="
					+ getElevation() + ", time=" + getTime() + "]";
		}

	}

	private static GpxType loadGpxFile(InputStream is) throws IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(GpxType.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder loader = factory.newDocumentBuilder();
			Document document = loader.parse(is);
			String namespace = document.getDocumentElement().getNamespaceURI();
			Object unmarshal;
			if ("http://www.topografix.com/GPX/1/1".equals(namespace)) {
				unmarshal = unmarshaller.unmarshal(document);
			} else if ("http://www.topografix.com/GPX/1/0".equals(namespace)) {
				unmarshal = xsltTransform(unmarshaller, document).getResult();
			} else {
				throw new IOException(
						"Expected GPX 1.0 or GPX1.1 namespace but found \n\""
								+ namespace + "\"");
			}
			@SuppressWarnings("unchecked")
			JAXBElement<GpxType> element = (JAXBElement<GpxType>) unmarshal;
			return element.getValue();
		} catch (JAXBException e) {
			throw new IOException(e);
		} catch (TransformerConfigurationException e) {
			throw new IOException(e);
		} catch (TransformerException e) {
			throw new IOException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new IOException(e);
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		} catch (SAXException e) {
			throw new IOException(e);
		}
	}

	private static JAXBResult xsltTransform(Unmarshaller unmarshaller,
			Document document) throws JAXBException, TransformerException,
			TransformerConfigurationException,
			TransformerFactoryConfigurationError {
		JAXBResult result = new JAXBResult(unmarshaller);
		StreamSource source = new StreamSource(
				GpxUnmarshaller.class
						.getResourceAsStream("/data/gpx/xsl/gpx10to11.xsl"));
		TransformerFactory.newInstance().newTransformer(source)
				.transform(new DOMSource(document), result);
		return result;
	}

	// TODO Change type to "SimpleTrack"
	public static Track loadTrack(InputStream inputStream) throws IOException {
		try {
			return convert(loadGpxFile(inputStream));
		} catch (Exception e) {
			throw new IOException("Error reading from InputStream "
					+ inputStream, e);
		}
	}

	public static Track loadTrack(File file) throws IOException {
		try {
			FileInputStream is = new FileInputStream(file);
			try {
				return convert(loadGpxFile(is));
			} catch (IOException e) {
				throw new IOException("Error loading " + file, e);
			} finally {
				is.close();
			}
		} catch (Exception e) {
			throw new IOException("Error reading file " + file, e);
		}
	}

	private static Track convert(GpxType gpxType) {
		List<Segment> segments = segments(gpxType);
		List<WayPoint> waypoints = waypoints(gpxType);
		Metadata metadata = new MetadataAdapter(gpxType.getMetadata());
		// we do not trust in bounds pre-calculated!
		List<LinkedTrackPoint> trkpts = getAllTrackpoints(segments);
		Metadata md = trkpts.isEmpty() ? null
				: new Delegate2MetadatForMinMaxLatLon(metadata, trkpts);
		return new DefaultTrack(null, md, waypoints, segments, null);
	}

	private static List<LinkedTrackPoint> getAllTrackpoints(
			List<Segment> segments) {
		List<LinkedTrackPoint> all = Lists.newArrayList();
		for (Segment segment : segments) {
			all.addAll(segment.getTrackpoints());
		}
		return all;
	}

	private static List<WayPoint> waypoints(GpxType gpx) {
		return toWaypoints(gpx.getWpt());
	}

	private static List<Segment> segments(GpxType gpx) {
		List<Segment> result = Lists.newArrayList();
		for (TrkType trkType : gpx.getTrk()) {
			for (TrksegType trksegType : trkType.getTrkseg()) {
				List<WptType> trkpt = trksegType.getTrkpt();
				if (!trkpt.isEmpty()) {
					result.add(convert(trkpt));
				}
			}
		}
		return result;
	}

	private static Segment convert(List<WptType> toConvert) {
		List<LinkedTrackPoint> wps = toLinked(toWaypoints(toConvert));
		return new DefaultSegment(wps, DefaultStatistics.ofWaypoints(wps));
	}

	private static List<WayPoint> toWaypoints(List<WptType> toConvert) {
		List<WayPoint> result = Lists.newArrayList();
		for (WptType wptType : toConvert) {
			result.add(DefaultWayPoint.of(new WayPointAdapter(wptType)));
		}
		return result;
	}

	public static List<LinkedTrackPoint> toLinked(
			Collection<? extends WayPoint> wps) {
		return fill(wps,
				Lists.<LinkedTrackPoint> newArrayListWithExpectedSize(wps
						.size()));
	}

	public static List<LinkedTrackPoint> toLinked(
			Iterable<? extends WayPoint> wps) {
		return fill(wps, Lists.<LinkedTrackPoint> newArrayList());
	}

	private static List<LinkedTrackPoint> fill(
			Iterable<? extends WayPoint> wps, List<LinkedTrackPoint> result) {
		WayPoint last = null;

		for (WayPoint next : wps) {
			if (last != null) {
				Link link = DefaultLink.of(last, next);
				result.add(DefaultLinkedWayPoint.of(last, link));
			}
			last = next;
		}
		if (last != null) {
			result.add(DefaultLinkedWayPoint.of(last, null));
		}
		return result;
	}

}
