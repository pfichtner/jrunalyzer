package com.github.pfichtner.jrunalyser.base.data;

public enum DistanceUnit {

	METERS {
		@Override
		public double toMeter(double distance) {
			return distance;
		}

		@Override
		public double toKilometer(double distance) {
			return distance / 1000;
		}

		@Override
		public double toFeet(double distance) {
			return distance * 3.280948;
		}

		@Override
		public double toMiles(double distance) {
			return distance * 0.000621371;
		}

		@Override
		public double convert(double d, DistanceUnit u) {
			return u.toMeter(d);
		}
	},
	KILOMETERS {
		@Override
		public double toMeter(double distance) {
			return distance * 1000;
		}

		@Override
		public double toKilometer(double distance) {
			return distance;
		}

		@Override
		public double toFeet(double distance) {
			return distance * 3280.947538;
		}

		@Override
		public double toMiles(double distance) {
			return distance * 0.6213712;
		}

		@Override
		public double convert(double d, DistanceUnit u) {
			return u.toKilometer(d);
		}
	},
	FEET {
		@Override
		public double toMeter(double distance) {
			return distance * 0.3048;
		}

		@Override
		public double toKilometer(double distance) {
			return distance * 0.0003048;
		}

		@Override
		public double toFeet(double distance) {
			return distance;
		}

		@Override
		public double toMiles(double distance) {
			return distance * 0.000189394;
		}

		@Override
		public double convert(double d, DistanceUnit u) {
			return u.toFeet(d);
		}
	},
	MILES {

		@Override
		public double toMeter(double distance) {
			return distance * 1609.344;
		}

		@Override
		public double toKilometer(double distance) {
			return distance * 1.60934;
		}

		@Override
		public double toFeet(double distance) {
			return distance * 5280;
		}

		@Override
		public double toMiles(double distance) {
			return distance;
		}

		@Override
		public double convert(double d, DistanceUnit u) {
			return u.toMiles(d);
		}

	};

	public abstract double toMeter(double distance);

	public abstract double toKilometer(double distance);

	public abstract double toFeet(double distance);

	public abstract double toMiles(double distance);

	public abstract double convert(double d, DistanceUnit u);

}
