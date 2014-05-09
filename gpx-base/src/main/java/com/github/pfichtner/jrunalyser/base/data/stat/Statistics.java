package com.github.pfichtner.jrunalyser.base.data.stat;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;

public interface Statistics {

	Statistics NULL = new Statistics() {

		@Override
		public LinkedTrackPoint getMinSpeed() {
			return null;
		}

		@Override
		public WayPoint getMinElevation() {
			return null;
		}

		@Override
		public LinkedTrackPoint getMaxSpeed() {
			return null;
		}

		@Override
		public WayPoint getMaxElevation() {
			return null;
		}

		@Override
		public Duration getDuration() {
			return null;
		}

		@Override
		public Distance getDistance() {
			return null;
		}

		@Override
		public int getDescent() {
			return 0;
		}

		@Override
		public Speed getAvgSpeed() {
			return null;
		}

		@Override
		public int getAscent() {
			return 0;
		}
	};

	// -----------------------------------------

	Distance getDistance();

	Duration getDuration();

	// -----------------------------------------

	WayPoint getMinElevation();

	WayPoint getMaxElevation();

	int getAscent();

	int getDescent();

	LinkedTrackPoint getMinSpeed();

	LinkedTrackPoint getMaxSpeed();

	Speed getAvgSpeed();

}
