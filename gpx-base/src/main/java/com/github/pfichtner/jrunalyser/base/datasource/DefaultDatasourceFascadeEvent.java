package com.github.pfichtner.jrunalyser.base.datasource;

import com.github.pfichtner.jrunalyser.base.data.track.Track;

public class DefaultDatasourceFascadeEvent implements DatasourceFascadeEvent {

	private final Type type;
	private final Track track;

	public DefaultDatasourceFascadeEvent(Type type, Track track) {
		this.type = type;
		this.track = track;
	}

	@Override
	public Type getType() {
		return this.type;
	}

	@Override
	public Track getTrack() {
		return this.track;
	}

}
