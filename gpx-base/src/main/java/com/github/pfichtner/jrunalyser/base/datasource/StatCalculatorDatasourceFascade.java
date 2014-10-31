package com.github.pfichtner.jrunalyser.base.datasource;

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.stat.Functions;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.Tracks;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.TrackComparators;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

public class StatCalculatorDatasourceFascade extends AbstractDatasourceFascade {

	private static final Logger log = LoggerFactory
			.getLogger(StatCalculatorDatasourceFascade.class);

	private final DatasourceFascade delegate;

	private final Function<Id, Track> loadTrack = new Function<Id, Track>() {
		@Override
		public Track apply(Id id) {
			try {
				return loadTrack(id);
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}
	};

	public StatCalculatorDatasourceFascade(DatasourceFascade delegate) {
		this.delegate = delegate;
	}

	@Override
	public Set<Id> getTrackIds() throws IOException {
		return this.delegate.getTrackIds();
	}

	@Override
	public Iterable<Id> getTrackIds(Date start, Date end) throws IOException {
		return this.delegate.getTrackIds(start, end);
	}

	@Override
	public Track loadTrack(Id id) throws IOException {
		return addStats(this.delegate.loadTrack(id));
	}

	@Override
	public Iterable<Track> loadTracks(Iterable<Id> ids) throws IOException {
		return Iterables.transform(ids, this.loadTrack);
	}

	@Override
	public List<Id> listTracks(SegmentationUnit segmentationUnit)
			throws IOException {
		return this.delegate.listTracks(segmentationUnit);
	}

	@Override
	public Optional<Statistics> loadBestSegment(Id id,
			SegmentationUnit segmentationUnit) throws IOException {
		return this.delegate.loadBestSegment(id, segmentationUnit);
	}

	@Override
	public Set<Id> getSimilarTracks(final Id id) throws IOException {
		return FluentIterable.from(getTrackIds()).filter(not(equalTo(id)))
				.transform(this.loadTrack).filter(similarTo(id))
				.transform(Functions.Tracks.id).toSet();
	}

	@Override
	public boolean isAwayEqReturn(Id id) {
		try {
			return Tracks.isAwayEqReturn(loadTrack(id));
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private Predicate<Track> similarTo(final Id id) throws IOException {
		return compose(equalTo(Integer.valueOf(0)), compareTrack(id));
	}

	private Function<Track, Integer> compareTrack(final Id id)
			throws IOException {
		return new Function<Track, Integer>() {
			Track ref = loadTrack(id);
			Comparator<Track> trackComparator = TrackComparators.byAttributes;

			@Override
			public Integer apply(Track track) {
				return Integer.valueOf(this.trackComparator.compare(this.ref,
						track));
			}
		};
	};

	@Override
	public Track addTrack(Track track) throws IOException {
		return addStats(this.delegate.addTrack(track));
	}

	@Override
	public Track removeTrack(Id id) throws IOException {
		return this.delegate.removeTrack(id);
	}

	@Override
	public Set<WayPoint> getCommonWaypoints() {
		return this.delegate.getCommonWaypoints();
	}

	// -------------------------------------------------------------

	private static Track addStats(Track track) {
		if (track.getStatistics() == null) {
			log.debug("Calculating Statistics for " + track);
			return new DefaultTrack(track.getId(), track.getMetadata(),
					track.getWaypoints(), track.getSegments(),
					DefaultStatistics.ofTrack(track));
		}
		return track;
	}
}
