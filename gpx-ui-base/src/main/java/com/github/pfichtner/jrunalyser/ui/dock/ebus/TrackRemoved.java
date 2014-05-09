package com.github.pfichtner.jrunalyser.ui.dock.ebus;

import com.github.pfichtner.jrunalyser.base.data.track.Track;

/**
 * Message is sent whenever a new Track was removed from a datasource.
 * 
 * @author Peter Fichtner
 */
@EventBusMessage
public class TrackRemoved {

	private final Track track;

	public TrackRemoved(Track gpxTrack) {
		this.track = gpxTrack;
	}

	public Track getTrack() {
		return this.track;
	}

}
