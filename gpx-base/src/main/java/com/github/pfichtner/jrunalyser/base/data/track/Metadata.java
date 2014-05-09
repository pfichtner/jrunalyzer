package com.github.pfichtner.jrunalyser.base.data.track;

public interface Metadata {

	String getName();

	String getDescription();

	Long getTime();

	double getMinLatitude();

	double getMinLongitude();

	double getMaxLatitude();

	double getMaxLongitude();

}
