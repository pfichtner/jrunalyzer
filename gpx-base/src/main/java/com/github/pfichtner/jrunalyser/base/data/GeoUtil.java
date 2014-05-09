package com.github.pfichtner.jrunalyser.base.data;

public final class GeoUtil {

	public interface BearingInfo {

		Distance getDistance();

		double getBearing();

	}

	public static class DefaultBearingInfo implements BearingInfo {

		private final Distance distance;
		private final double bearing;

		public DefaultBearingInfo(Distance distance, double bearing) {
			this.distance = distance;
			this.bearing = bearing;
		}

		public Distance getDistance() {
			return this.distance;
		}

		public double getBearing() {
			return this.bearing;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(this.bearing);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result
					+ ((this.distance == null) ? 0 : this.distance.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DefaultBearingInfo other = (DefaultBearingInfo) obj;
			if (Double.doubleToLongBits(this.bearing) != Double
					.doubleToLongBits(other.bearing))
				return false;
			if (this.distance == null) {
				if (other.distance != null)
					return false;
			} else if (!this.distance.equals(other.distance))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "DefaultBearingInfo [distance=" + this.distance
					+ ", bearing=" + this.bearing + "]";
		}

	}

	/** degrees to radians **/
	private static final double D2R = Math.PI / 180;

	/** Radius of the earth in m */
	private static final Distance EARTH_RADIUS = DefaultDistance.of(6378160,
			DistanceUnit.METERS);

	/** Radius of the earth in m */
	private static final double EARTH_RADIUS_M = EARTH_RADIUS
			.getValue(DistanceUnit.METERS);

	private GeoUtil() {
		super();
	}

	public static Distance calcDistance(Coordinate coord1, Coordinate coord2) {
		return DefaultDistance.of(
				calcMeters(coord1.getLatitude(), coord1.getLongitude(),
						coord2.getLatitude(), coord2.getLongitude()),
				DistanceUnit.METERS);
	}

	public static double calcMeters(double lat1, double lon1, double lat2,
			double lon2) {
		double dLat = (lat2 - lat1) * D2R;
		double dLon = (lon2 - lon1) * D2R;
		double sin = Math.sin(dLat / 2);
		double cos = Math.cos(lat1 * D2R);
		double a = sin * sin + cos * Math.cos(lat2 * D2R) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		return EARTH_RADIUS_M
				* (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
	}

	public static double bearing(Coordinate coord1, Coordinate coord2) {
		return bearing(coord1.getLatitude(), coord1.getLongitude(),
				coord2.getLatitude(), coord2.getLongitude());
	}

	/**
	 * Computes the bearing in degrees between two points on Earth.
	 * 
	 * @param lat1
	 *            Latitude of the first point
	 * @param lon1
	 *            Longitude of the first point
	 * @param lat2
	 *            Latitude of the second point
	 * @param lon2
	 *            Longitude of the second point
	 * @return Bearing between the two points in degrees. A value of 0 means due
	 *         north.
	 */
	public static double bearing(double lat1, double lon1, double lat2,
			double lon2) {
		double lat1Rad = Math.toRadians(lat1);
		double lat2Rad = Math.toRadians(lat2);
		double deltaLonRad = Math.toRadians(lon2 - lon1);

		double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
		double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad)
				* Math.cos(lat2Rad) * Math.cos(deltaLonRad);
		return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
	}

	public static BearingInfo bearingInfo(Coordinate coord1, Coordinate coord2) {
		return new DefaultBearingInfo(calcDistance(coord1, coord2), bearing(
				coord1, coord2));
	}

	public static Coordinate project(BearingInfo bearing, Coordinate startCoords) {
		double[] coords = project(
				bearing.getDistance().getValue(DistanceUnit.METERS),
				bearing.getBearing(), startCoords.getLatitude(),
				startCoords.getLongitude());
		return new DefaultCoordinate(coords[0], coords[1]);
	}

	/**
	 * Waypoint projection using haversine formula
	 * 
	 * http://en.wikipedia.org/wiki/Haversine_formula
	 * 
	 * See discussion here for further information:
	 * http://www.movable-type.co.uk/scripts/latlong.html
	 * 
	 */
	private static double[] project(double meters, double bearing,
			double startLat, double startLon) {

		double distanceRad = meters / EARTH_RADIUS_M;
		double bearingRad = Math.toRadians(bearing);
		double startLatRad = Math.toRadians(startLat);
		double startLonRad = Math.toRadians(startLon);

		double endLat = Math.asin(Math.sin(startLatRad) * Math.cos(distanceRad)
				+ Math.cos(startLatRad) * Math.sin(distanceRad)
				* Math.cos(bearingRad));

		double endLon = startLonRad
				+ Math.atan2(
						Math.sin(bearingRad) * Math.sin(distanceRad)
								* Math.cos(startLatRad),
						Math.cos(distanceRad) - Math.sin(startLatRad)
								* Math.sin(endLat));

		// Adjust projections crossing the 180th meridian:
		double endLonDeg = Math.toDegrees(endLon);

		if (endLonDeg > 180 || endLonDeg < -180) {
			endLonDeg = endLonDeg % 360;
			if (endLonDeg > 180) {
				// Just in case we circle the earth more than once.
				endLonDeg = endLonDeg - 360;
			} else if (endLonDeg < -180) {
				endLonDeg = endLonDeg + 360;
			}
		}

		return new double[] { Math.toDegrees(endLat), endLonDeg };

	}

}
