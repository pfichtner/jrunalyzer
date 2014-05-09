package com.github.pfichtner.jrunalyser.base.datasource;

import com.github.pfichtner.jrunalyser.base.data.track.Track;

public interface DatasourceFascadeEvent {

	public enum Type {
		ADDED, REMOVED, MODIFIED;
	}

	DatasourceFascadeEvent.Type getType();

	Track getTrack();

}