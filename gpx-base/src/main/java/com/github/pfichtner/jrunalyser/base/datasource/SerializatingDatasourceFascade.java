package com.github.pfichtner.jrunalyser.base.datasource;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.stat.DefaultStatistics;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

public class SerializatingDatasourceFascade extends AbstractDatasourceFascade {

	private static final Logger log = LoggerFactory
			.getLogger(SerializatingDatasourceFascade.class);

	private final DatasourceFascade delegate;
	private final File cacheDir;

	private Function<Id, Track> loadTrack = new Function<Id, Track>() {
		@Override
		public Track apply(Id id) {
			try {
				return loadTrack(id);
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}
	};

	public SerializatingDatasourceFascade(File baseDir,
			DatasourceFascade delegate) {
		this.cacheDir = new File(baseDir, ".cache");
		this.delegate = delegate;
		checkState(this.cacheDir.exists() || this.cacheDir.mkdirs());
	}

	public Set<Id> getTrackIds() throws IOException {
		return this.delegate.getTrackIds();
	}

	public Iterable<Id> getTrackIds(Date start, Date end) throws IOException {
		return this.delegate.getTrackIds(start, end);
	}

	public Track loadTrack(Id id) throws IOException {
		return cache(this.delegate.loadTrack(id));
	}

	@Override
	public Iterable<Track> loadTracks(Iterable<Id> ids) throws IOException {
		return Iterables.transform(ids, this.loadTrack);
	}

	public List<Id> listTracks(SegmentationUnit segmentationUnit)
			throws IOException {
		return this.delegate.listTracks(segmentationUnit);
	}

	public Optional<Statistics> loadBestSegment(Id id,
			SegmentationUnit segmentationUnit) throws IOException {
		Optional<Statistics> loadBestSegment = loadCachedBestSegment(id,
				segmentationUnit);
		if (!loadBestSegment.isPresent()) {
			loadBestSegment = this.delegate.loadBestSegment(id,
					segmentationUnit);
			if (loadBestSegment.isPresent()) {
				saveCachedBestSegment(id, segmentationUnit,
						loadBestSegment.get());
			}
		}
		return loadBestSegment;
	}

	public Set<Id> getSimilarTracks(final Id id) throws IOException {
		return this.delegate.getSimilarTracks(id);
	}

	@Override
	public boolean isAwayEqReturn(Id id) {
		return this.delegate.isAwayEqReturn(id);
	}

	public Track addTrack(Track track) throws IOException {
		return cache(this.delegate.addTrack(track));
	}

	public Track removeTrack(Id id) throws IOException {
		return this.delegate.removeTrack(id);
	}
	
	@Override
	public Set<WayPoint> getCommonWaypoints() {
		return this.delegate.getCommonWaypoints();
	}

	// -------------------------------------------------------------

	private Track cache(Track track) {
		Statistics statistics = null;
		if (track.getStatistics() == null) {
			String retained = CharMatcher.JAVA_LETTER_OR_DIGIT
					.retainFrom(String.valueOf(track.getId()));
			File cache = new File(this.cacheDir, "trackstat_" + retained);
			// TODO Add uptodatecheck for cache file
			statistics = cache.exists() ? (Statistics) read(cache) : write(
					cache, DefaultStatistics.ofTrack(track));
		}
		return new DefaultTrack(track.getId(), track.getMetadata(),
				track.getWaypoints(), track.getSegments(), statistics);
	}

	private static <T> T write(File cache, T object) {
		try {
			ObjectOutputStream ois = new ObjectOutputStream(
					new FileOutputStream(cache));
			try {
				ois.writeObject(object);
				log.debug("Serialized {}", cache);
			} finally {
				ois.close();
			}
		} catch (FileNotFoundException e) {
			throw Throwables.propagate(e);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
		return object;
	}

	private static Object read(File cache) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					cache));
			try {
				return ois.readObject();
			} finally {
				log.debug("Deserialized {}", cache);
				ois.close();
			}
		} catch (FileNotFoundException e) {
			throw Throwables.propagate(e);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		} catch (ClassNotFoundException e) {
			throw Throwables.propagate(e);
		}
	}

	// ----------------------------------------------------------------------------

	private Optional<Statistics> loadCachedBestSegment(Id id,
			SegmentationUnit unit) {
		File cache = createBestSegmentDataFile(id, unit);
		return cache.exists() ? Optional.of((Statistics) read(cache))
				: Optional.<Statistics> absent();
	}

	private Statistics saveCachedBestSegment(Id id, SegmentationUnit unit,
			Statistics bestSegment) {
		return write(createBestSegmentDataFile(id, unit), bestSegment);
	}

	private File createBestSegmentDataFile(Id id, SegmentationUnit unit) {
		String retained = CharMatcher.JAVA_LETTER_OR_DIGIT.retainFrom(String
				.valueOf(id) + unit);
		File cache = new File(this.cacheDir, "segmentdata_" + retained);
		return cache;
	}

}
