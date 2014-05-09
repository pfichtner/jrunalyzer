package com.github.pfichtner.jrunalyser.base.data;

public interface Coordinate {

	Coordinate ZERO_ZERO = new Coordinate() {

		@Override
		public double getLongitude() {
			return 0;
		}

		@Override
		public double getLatitude() {
			return 0;
		}

	};

	public abstract double getLatitude();

	public abstract double getLongitude();

}