package com.github.pfichtner.jrunalyser.ui.base;

import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;

public final class MetricSettings implements Settings {

	public static final Settings INSTANCE = new MetricSettings();
	private static final SpeedUnit KM_PER_H = new SpeedUnit() {

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.HOURS;
		}

		@Override
		public DistanceUnit getDistanceUnit() {
			return DistanceUnit.KILOMETERS;
		}
	};
	private static final PaceUnit MIN_PER_KM = new PaceUnit() {

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MINUTES;
		}

		@Override
		public DistanceUnit getDistanceUnit() {
			return DistanceUnit.KILOMETERS;
		}
	};

	private MetricSettings() {
		super();
	}

	@Override
	public SpeedUnit getSpeedUnit() {
		return KM_PER_H;
	}

	@Override
	public PaceUnit getPaceUnit() {
		return MIN_PER_KM;
	}

	@Override
	public DistanceUnit getDistanceUnit() {
		return DistanceUnit.KILOMETERS;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.MINUTES;
	}

}
