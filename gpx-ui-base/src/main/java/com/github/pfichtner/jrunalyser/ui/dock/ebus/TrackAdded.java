package com.github.pfichtner.jrunalyser.ui.dock.ebus;

import com.github.pfichtner.jrunalyser.base.data.track.Track;

/**
 * Message is sent whenever a new Track was added to a datasource.
 * 
 * @author Peter Fichtner
 */
@EventBusMessage
public class TrackAdded {

	private final Track track;

	public TrackAdded(Track gpxTrack) {
		this.track = gpxTrack;
	}

	public Track getTrack() {
		return this.track;
	}

}
