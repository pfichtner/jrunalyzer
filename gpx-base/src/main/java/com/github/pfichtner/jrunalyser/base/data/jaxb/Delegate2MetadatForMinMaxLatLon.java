package com.github.pfichtner.jrunalyser.base.data.jaxb;

import static com.github.pfichtner.jrunalyser.base.data.stat.Orderings.latitudeOrdering;
import static com.github.pfichtner.jrunalyser.base.data.stat.Orderings.longitudeOrdering;

import java.math.BigDecimal;
import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Metadata;
import com.topografix.gpx._1._1.BoundsType;

public class Delegate2MetadatForMinMaxLatLon implements Metadata {

	private final Metadata metadata;
	private final BoundsType boundsType;

	public Delegate2MetadatForMinMaxLatLon(Metadata delegate,
			BoundsType boundsType) {
		this.metadata = delegate;
		this.boundsType = boundsType;
	}

	public Delegate2MetadatForMinMaxLatLon(Metadata delegate,
			List<LinkedTrackPoint> wayPoints) {
		this(delegate, createBounds(wayPoints));
	}

	// ----------------------------------------------------------------

	private static BoundsType createBounds(List<LinkedTrackPoint> wayPoints) {
		BoundsType boundsType = new BoundsType();
		boundsType.setMinlat(BigDecimal.valueOf(latitudeOrdering.min(wayPoints)
				.getLatitude()));
		boundsType.setMaxlat(BigDecimal.valueOf(latitudeOrdering.max(wayPoints)
				.getLatitude()));
		boundsType.setMinlon(BigDecimal.valueOf(longitudeOrdering
				.min(wayPoints).getLongitude()));
		boundsType.setMaxlon(BigDecimal.valueOf(longitudeOrdering
				.max(wayPoints).getLongitude()));
		return boundsType;
	}

	// ----------------------------------------------------------------

	public String getName() {
		return this.metadata.getName();
	}

	public String getDescription() {
		return this.metadata.getDescription();
	}

	public Long getTime() {
		return this.metadata.getTime();
	}

	// ----------------------------------------------------------------

	public double getMinLatitude() {
		return this.boundsType.getMinlat().doubleValue();
	}

	public double getMinLongitude() {
		return this.boundsType.getMinlon().doubleValue();
	}

	public double getMaxLatitude() {
		return this.boundsType.getMaxlat().doubleValue();
	}

	public double getMaxLongitude() {
		return this.boundsType.getMaxlon().doubleValue();
	}

}
