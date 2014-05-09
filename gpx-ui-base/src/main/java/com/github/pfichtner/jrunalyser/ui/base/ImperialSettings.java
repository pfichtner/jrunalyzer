package com.github.pfichtner.jrunalyser.ui.base;

import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;

public final class ImperialSettings implements Settings {

	public static final Settings INSTANCE = new ImperialSettings();
	private static final SpeedUnit MI_PER_H = new SpeedUnit() {

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.HOURS;
		}

		@Override
		public DistanceUnit getDistanceUnit() {
			return DistanceUnit.MILES;
		}
	};
	private static final PaceUnit MI_PER_MIN = new PaceUnit() {

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MINUTES;
		}

		@Override
		public DistanceUnit getDistanceUnit() {
			return DistanceUnit.MILES;
		}
	};

	private ImperialSettings() {
		super();
	}

	@Override
	public SpeedUnit getSpeedUnit() {
		return MI_PER_H;
	}

	@Override
	public PaceUnit getPaceUnit() {
		return MI_PER_MIN;
	}

	@Override
	public DistanceUnit getDistanceUnit() {
		return DistanceUnit.MILES;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.MINUTES;
	}

}
