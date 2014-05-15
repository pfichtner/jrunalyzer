package com.github.pfichtner.jrunalyser.base.datasource;

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.stat.Orderings;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.Tracks;
import com.github.pfichtner.jrunalyser.base.data.track.comparator.TrackComparators;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascadeEvent.Type;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

public class CachingDatasourceFascadeProxy extends AbstractDatasourceFascade {

	private final DatasourceFascade delegate;

	private Set<Id> trackIds;

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

	private LoadingCache<Id, Track> tracks = CacheBuilder.newBuilder().build(
			new CacheLoader<Id, Track>() {
				@Override
				public Track load(Id id) throws Exception {
					return CachingDatasourceFascadeProxy.this.delegate
							.loadTrack(id);
				}
			});

	private LoadingCache<SegmentationUnit, List<Id>> topTracksIds = CacheBuilder
			.newBuilder().build(new CacheLoader<SegmentationUnit, List<Id>>() {
				@Override
				public List<Id> load(SegmentationUnit segmentationUnit)
						throws Exception {
					return CachingDatasourceFascadeProxy.this.delegate
							.listTracks(segmentationUnit);
				}
			});

	private LoadingCache<Id, LoadingCache<SegmentationUnit, Optional<Statistics>>> segmentData = CacheBuilder
			.newBuilder()
			.build(new CacheLoader<Id, LoadingCache<SegmentationUnit, Optional<Statistics>>>() {
				@Override
				public LoadingCache<SegmentationUnit, Optional<Statistics>> load(
						final Id id) throws Exception {
					CacheLoader<SegmentationUnit, Optional<Statistics>> loader = new CacheLoader<SegmentationUnit, Optional<Statistics>>() {

						@Override
						public Optional<Statistics> load(
								SegmentationUnit segmentationUnit)
								throws Exception {
							return CachingDatasourceFascadeProxy.this.delegate
									.loadBestSegment(id, segmentationUnit);
						}
					};
					return CacheBuilder.newBuilder().build(loader);
				}
			});

	private LoadingCache<Id, Set<Id>> similarTrackIds = CacheBuilder
			.newBuilder().build(new CacheLoader<Id, Set<Id>>() {
				@Override
				public Set<Id> load(Id id) throws Exception {
					return Sets
							.filter(getTrackIds(),
									com.google.common.base.Predicates
											.and(compose(
													similar(id),
													CachingDatasourceFascadeProxy.this.loadTrack),
													not(equalTo(id))));
				}

				private Predicate<Track> similar(final Id id)
						throws IOException {
					final Track ref = loadTrack(id);
					return com.google.common.base.Predicates.compose(
							equalTo(Integer.valueOf(0)),
							new Function<Track, Integer>() {
								Comparator<Track> trackComparator = TrackComparators.byAttributes;

								@Override
								public Integer apply(Track track) {
									return Integer.valueOf(this.trackComparator
											.compare(ref, track));
								}
							});
				};

			});

	private LoadingCache<Id, Boolean> isAwayEqReturn = CacheBuilder
			.newBuilder().build(new CacheLoader<Id, Boolean>() {
				@Override
				public Boolean load(Id id) throws Exception {
					return Boolean
							.valueOf(CachingDatasourceFascadeProxy.this.delegate
									.isAwayEqReturn(id));
				};

			});

	private Set<WayPoint> commonWaypoints;

	public CachingDatasourceFascadeProxy(DatasourceFascade datasourceFascade) {
		this.delegate = datasourceFascade;
		initCaches();
	}

	public Set<Id> getTrackIds() throws IOException {
		return this.trackIds;
	}

	@Override
	public Iterable<Id> getTrackIds(Date start, Date end) throws IOException {
		Iterable<Track> tracks = transform(getTrackIds(), this.loadTrack);
		Iterable<Track> filtered = filter(tracks, fromTo(this, start, end));
		return transform(
				Orderings.time.sortedCopy(filtered),
				com.github.pfichtner.jrunalyser.base.data.stat.Functions.Tracks.id);
	}

	public static Predicate<Track> fromTo(final DatasourceFascade dsf,
			final Date start, final Date end) {
		return new Predicate<Track>() {
			@Override
			public boolean apply(Track track) {
				return Range.open(start, end).contains(
						new Date(Tracks.getStartPoint(track).getTime()
								.longValue()));
			}
		};
	}

	public Track loadTrack(Id id) throws IOException {
		return this.tracks.getUnchecked(id);
	}

	@Override
	public Iterable<Track> loadTracks(Iterable<Id> ids) throws IOException {
		return Iterables.transform(ids, this.loadTrack);
	}

	public Track addTrack(Track track) throws IOException {
		Track result = this.delegate.addTrack(track);
		initCaches();
		fire(new DefaultDatasourceFascadeEvent(Type.ADDED, result));
		return result;
	}

	@Override
	public Track removeTrack(Id id) throws IOException {
		Track result = this.delegate.removeTrack(id);
		initCaches();
		fire(new DefaultDatasourceFascadeEvent(Type.REMOVED, result));
		return result;
	}

	public List<Id> listTracks(SegmentationUnit segmentationUnit)
			throws IOException {
		return this.topTracksIds.getUnchecked(segmentationUnit);
	}

	@Override
	public Optional<Statistics> loadBestSegment(Id id,
			SegmentationUnit segmentationUnit) throws IOException {
		return this.segmentData.getUnchecked(id).getUnchecked(segmentationUnit);
	}

	// -------------------------------------------------------------

	public Set<Id> getSimilarTracks(Id id) {
		return this.similarTrackIds.getUnchecked(id);
	};

	@Override
	public boolean isAwayEqReturn(Id id) {
		return this.isAwayEqReturn.getUnchecked(id).booleanValue();
	};

	// -------------------------------------------------------------

	public Set<WayPoint> getCommonWaypoints() {
		synchronized (this) {
			if (this.commonWaypoints == null) {
				this.commonWaypoints = this.delegate.getCommonWaypoints();

			}
		}
		return this.commonWaypoints;
	};

	// -------------------------------------------------------------

	private void initCaches() {
		try {
			this.trackIds = ImmutableSet.copyOf(this.delegate.getTrackIds());
			// this.tracks.invalidateAll();
			this.topTracksIds.invalidateAll();
			// this.segmentData.invalidateAll();
			this.similarTrackIds.invalidateAll();
			// this.isAwayEqReturn.invalidateAll();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

}
