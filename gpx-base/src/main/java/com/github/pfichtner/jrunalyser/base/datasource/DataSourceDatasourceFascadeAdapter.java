package com.github.pfichtner.jrunalyser.base.datasource;

import static com.github.pfichtner.jrunalyser.base.datasource.CachingDatasourceFascadeProxy.fromTo;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterables.tryFind;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.floater.HighlightableSegment;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenter;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenters;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.stat.Orderings;
import com.github.pfichtner.jrunalyser.base.data.stat.Predicates;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.Tracks;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.TrackComparators;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascadeEvent.Type;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mathieubolla.guava.FluentParallelIterable;

/**
 * A DatasourceFascade that adapts a {@link Datasource} to
 * {@link DatasourceFascade} by calculating all attributes. Since nothing gets
 * cached this adapter should be used in conjunction with
 * {@link com.github.pfichtner.jrunalyser.base.datasource.CachingDatasourceFascadeProxy}.
 * 
 * @author Peter Fichtner
 */
public class DataSourceDatasourceFascadeAdapter extends
		AbstractDatasourceFascade {

	private final Datasource datasource;

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

	public DataSourceDatasourceFascadeAdapter(Datasource datasource) {
		this.datasource = datasource;
	}

	@Override
	public Set<Id> getTrackIds() throws IOException {
		return this.datasource.getTrackIds();
	}

	@Override
	public Iterable<Id> getTrackIds(Date start, Date end) throws IOException {
		List<Track> tracks = Orderings.time.sortedCopy(FluentParallelIterable
				.from(getTrackIds()).parallel().transform(this.loadTrack)
				.filter(fromTo(this, start, end)));
		return transform(tracks, com.github.pfichtner.jrunalyser.base.data.stat.Functions.Tracks.id);
	}

	@Override
	public Track addTrack(Track track) throws IOException {
		Track result = this.datasource.addTrack(track);
		fire(new DefaultDatasourceFascadeEvent(Type.ADDED, result));
		return result;
	}

	@Override
	public Track removeTrack(Id id) throws IOException {
		Track result = this.datasource.removeTrack(id);
		fire(new DefaultDatasourceFascadeEvent(Type.REMOVED, result));
		return result;
	}

	public Track loadTrack(Id id) throws IOException {
		Track loadTrack = this.datasource.loadTrack(id);
		return new DefaultTrack(loadTrack.getId(), loadTrack.getMetadata(),
				loadTrack.getWaypoints(), loadTrack.getSegments(), null);
	}

	@Override
	public Iterable<Track> loadTracks(Iterable<Id> ids) throws IOException {
		return Iterables.transform(ids, this.loadTrack);
	}

	@Override
	public Optional<Statistics> loadBestSegment(Id id,
			SegmentationUnit segmentationUnit) throws IOException {
		Function<Id, Track> loadSegmented = Functions.compose(
				createSegmentFunc(Segmenters
						.getFloatingSegmenter(segmentationUnit)),
				this.loadTrack);
		Optional<HighlightableSegment> segment = getHlSegment(loadSegmented
				.apply(id));
		return segment.isPresent() ? Optional.of(getFixedStats(
				segmentationUnit, segment.get())) : Optional
				.<Statistics> absent();
	}

	private Statistics getFixedStats(SegmentationUnit segmentationUnit,
			HighlightableSegment segment) {
		Statistics statistics;
		if (segmentationUnit instanceof Duration) {
			statistics = new FixedStatistics((Duration) segmentationUnit,
					segment.getStatistics());
		} else if (segmentationUnit instanceof Distance) {
			statistics = new FixedStatistics((Distance) segmentationUnit,
					segment.getStatistics());
		} else {
			throw new IllegalStateException("Cannot handle " + segmentationUnit);
		}
		return DefaultStatistics.copyOf(statistics);
	}

	private static Optional<HighlightableSegment> getHlSegment(
			Track segmentedTrack) {
		return tryFind(
				filter(segmentedTrack.getSegments(), HighlightableSegment.class),
				Predicates.HighlightableSegments.isHighligted);
	}

	@Override
	public List<Id> listTracks(SegmentationUnit segmentationUnit)
			throws IOException {
		List<Track> in = loadOrdered(segmentationUnit);
		return FluentParallelIterable.from(in).parallel()
				.transform(com.github.pfichtner.jrunalyser.base.data.stat.Functions.Tracks.id)
				.toList();
	}

	private List<Track> loadOrdered(final SegmentationUnit segmentationUnit)
			throws IOException {
		return FluentIterable
				.from(getSegmented(Segmenters
						.getFloatingSegmenter(segmentationUnit))).toSortedList(
						Orderings.highlightedSpeedOrdering);
	}

	// ------------------------------------------------------------------------

	private Iterable<Track> getSegmented(Segmenter segmenter)
			throws IOException {
		return FluentParallelIterable.from(getTrackIds()).parallel()
				.transform(this.loadTrack)
				.transform(createSegmentFunc(segmenter));
	}

	private static Function<Track, Track> createSegmentFunc(
			final Segmenter segmenter) {
		return new Function<Track, Track>() {
			@Override
			public Track apply(Track track) {
				return segmenter.segment(track);
			}
		};
	}

	// ----------------------------------------------------------------------

	public Set<Id> getSimilarTracks(final Id id) throws IOException {
		return Sets
				.filter(getTrackIds(), com.google.common.base.Predicates.and(
						compose(similar(id), this.loadTrack), not(equalTo(id))));
	}

	@Override
	public boolean isAwayEqReturn(Id id) {
		try {
			return Tracks.isAwayEqReturn(loadTrack(id));
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public Set<WayPoint> getCommonWaypoints() {
		return this.datasource.getCommonWaypoints();
	}

	private Predicate<Track> similar(final Id id) throws IOException {
		return com.google.common.base.Predicates.compose(
				equalTo(Integer.valueOf(0)), new Function<Track, Integer>() {
					Track ref = loadTrack(id);
					// we cannot provide byAttributes since this will lead to
					// accesses to the statistics we don't provide
					Comparator<Track> trackComparator = TrackComparators.byDescription;

					@Override
					public Integer apply(Track track) {
						return Integer.valueOf(this.trackComparator.compare(
								this.ref, track));
					}
				});
	};

}
