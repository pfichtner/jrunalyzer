package com.github.pfichtner.jrunalyser.base.datasource;

import java.io.IOException;
import java.util.Set;

import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;

public interface Datasource {

	Set<Id> getTrackIds() throws IOException;

	Track loadTrack(Id id) throws IOException;

	Iterable<Track> loadTracks(Iterable<Id> ids) throws IOException;

	Track addTrack(Track track) throws IOException;

	Track removeTrack(Id id) throws IOException;

	Set<WayPoint> getCommonWaypoints();

}
