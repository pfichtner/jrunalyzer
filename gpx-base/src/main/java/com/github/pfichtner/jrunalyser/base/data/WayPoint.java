package com.github.pfichtner.jrunalyser.base.data;

public interface WayPoint extends Coordinate {

	String getName();
	
	Integer getElevation();

	// TODO change to Date!?
	Long getTime();

}
