package com.github.pfichtner.jrunalyser.base.datasource;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;

@ThreadSafe
public class InMemoryDataSource implements Datasource {

	private final Map<Id, Track> data = Maps.newConcurrentMap();
	private final Set<WayPoint> commonWaypoints = Sets.newCopyOnWriteArraySet();

	@Override
	public Set<Id> getTrackIds() {
		return this.data.keySet();
	}

	@Override
	public Track loadTrack(Id id) {
		return this.data.get(id);
	}

	@Override
	public Iterable<Track> loadTracks(final Iterable<Id> ids)
			throws IOException {
		return new Iterable<Track>() {
			@Override
			public Iterator<Track> iterator() {
				return new UnmodifiableIterator<Track>() {

					Iterator<Id> delegate = ids.iterator();

					@Override
					public boolean hasNext() {
						return this.delegate.hasNext();
					}

					@Override
					public Track next() {
						return loadTrack(this.delegate.next());
					}
				};
			}
		};
	}

	@Override
	public Track addTrack(Track track) {
		Id id = track.getId();
		DefaultTrack result = new DefaultTrack(id, track.getMetadata(),
				track.getWaypoints(), track.getSegments(),
				track.getStatistics());
		this.data.put(id, result);
		return result;
	}

	@Override
	public Track removeTrack(Id id) {
		return this.data.remove(id);
	}

	@Override
	public Set<WayPoint> getCommonWaypoints() {
		return this.commonWaypoints;
	}

	public void addCommonWaypoint(WayPoint wp) {
		this.commonWaypoints.add(wp);
	}

}