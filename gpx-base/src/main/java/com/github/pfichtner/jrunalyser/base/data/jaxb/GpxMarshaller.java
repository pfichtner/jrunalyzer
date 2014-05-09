package com.github.pfichtner.jrunalyser.base.data.jaxb;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Metadata;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Throwables;
import com.topografix.gpx._1._1.GpxType;
import com.topografix.gpx._1._1.MetadataType;
import com.topografix.gpx._1._1.ObjectFactory;
import com.topografix.gpx._1._1.TrkType;
import com.topografix.gpx._1._1.TrksegType;
import com.topografix.gpx._1._1.WptType;

public final class GpxMarshaller {

	private GpxMarshaller() {
		super();
	}

	public static void writeTrack(Writer writer, Track track)
			throws IOException {
		try {
			createMarshaller().marshal(createJaxbElement(toGpxType(track)),
					writer);
		} catch (Exception e) {
			throw new IOException("Error writing to " + writer, e);
		}
	}

	public static void writeTrack(File file, Track track) throws IOException {
		try {
			createMarshaller().marshal(createJaxbElement(toGpxType(track)),
					file);
		} catch (Exception e) {
			throw new IOException("Error writing " + file, e);
		}
	}

	private static JAXBElement<GpxType> createJaxbElement(GpxType gpxType) {
		return new ObjectFactory().createGpx(gpxType);
	}

	private static Marshaller createMarshaller() throws JAXBException,
			PropertyException {
		Marshaller marshaller = JAXBContext.newInstance(GpxType.class)
				.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		return marshaller;
	}

	private static GpxType toGpxType(Track track) {
		GpxType gpxType = new GpxType();
		gpxType.setMetadata(createMetaData(track.getMetadata()));
		TrkType trkType = new TrkType();
		TrksegType trksegType = new TrksegType();
		List<WptType> trkpt = trksegType.getTrkpt();
		for (LinkedTrackPoint ltrkpts : (List<? extends LinkedTrackPoint>) track
				.getTrackpoints()) {
			trkpt.add(createWptType(ltrkpts));
		}

		trkType.getTrkseg().add(trksegType);
		gpxType.getTrk().add(trkType);
		return gpxType;
	}

	private static MetadataType createMetaData(Metadata metadata) {
		MetadataType result = new MetadataType();
		result.setDesc(metadata.getDescription());
		result.setName(metadata.getName());
		if (metadata.getTime() != null) {
			result.setTime(toXMLGregorianCalendar(metadata.getTime()));
		}
		return result;
	}

	private static WptType createWptType(LinkedTrackPoint wp) {
		WptType result = new WptType();
		if (wp.getElevation() != null) {
			result.setEle(new BigDecimal(wp.getElevation().toString()));
		}
		result.setLat(BigDecimal.valueOf(wp.getLatitude()));
		result.setLon(BigDecimal.valueOf(wp.getLongitude()));
		result.setTime(toXMLGregorianCalendar(wp.getTime()));
		return result;
	}

	private static XMLGregorianCalendar toXMLGregorianCalendar(Long time) {
		return time == null ? null : toXMLGregorianCalendar(new Date(
				time.longValue()));
	}

	private static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
		try {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			XMLGregorianCalendar newXMLGregorianCalendar = DatatypeFactory
					.newInstance().newXMLGregorianCalendar(cal);
			return newXMLGregorianCalendar;
		} catch (DatatypeConfigurationException e) {
			throw Throwables.propagate(e);
		}
	}

}
