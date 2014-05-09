package com.github.pfichtner.jrunalyser.ui.base;

import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;

// TODO http://docs.oracle.com/javase/tutorial/i18n/resbundle/list.html
public interface Settings {

	public interface SpeedUnit {

		DistanceUnit getDistanceUnit();

		TimeUnit getTimeUnit();

	}

	public interface PaceUnit {

		TimeUnit getTimeUnit();

		DistanceUnit getDistanceUnit();

	}

	SpeedUnit getSpeedUnit();

	PaceUnit getPaceUnit();

	DistanceUnit getDistanceUnit();

	TimeUnit getTimeUnit();

}
