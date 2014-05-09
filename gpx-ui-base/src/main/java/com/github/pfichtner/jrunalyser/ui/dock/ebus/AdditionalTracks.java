package com.github.pfichtner.jrunalyser.ui.dock.ebus;

import java.util.List;

import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.collect.ImmutableList;

@EventBusMessage
public class AdditionalTracks {

	private final List<Track> tracks;

	public AdditionalTracks(List<Track> tracks) {
		super();
		this.tracks = ImmutableList.copyOf(tracks);
	}

	public List<Track> getTracks() {
		return this.tracks;
	}

}
