package com.github.pfichtner.jrunalyser.ui.dock.ebus;

import com.github.pfichtner.jrunalyser.base.data.track.Track;

@EventBusMessage
public class TrackLoaded {

	private final Track track;

	public TrackLoaded(Track gpxTrack) {
		this.track = gpxTrack;
	}

	public Track getTrack() {
		return this.track;
	}

}
