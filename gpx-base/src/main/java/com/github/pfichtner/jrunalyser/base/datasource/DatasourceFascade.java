package com.github.pfichtner.jrunalyser.base.datasource;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Optional;

public interface DatasourceFascade {

	Set<Id> getTrackIds() throws IOException;

	/**
	 * Returns an <b>ordered</b> Iterable containing the {@link Id}s in the
	 * matching period.
	 * 
	 * @param start
	 *            start date
	 * @param end
	 *            end date
	 * @return Iterable containing the {@link Id}s in the matching period in
	 *         correct order
	 * @throws IOException
	 */
	Iterable<Id> getTrackIds(Date start, Date end) throws IOException;

	Track loadTrack(Id id) throws IOException;

	Iterable<Track> loadTracks(Iterable<Id> ids) throws IOException;

	// --------------------------------------------------------------------

	/**
	 * Returns the {@link Id}s in descending order, ordered by the passed
	 * {@link SegmentationUnit}.
	 * 
	 * @param segmentationUnit
	 *            the SegmentationUnit to list
	 * @return List of Ids
	 * @throws IOException
	 */
	List<Id> listTracks(SegmentationUnit segmentationUnit) throws IOException;

	Optional<Statistics> loadBestSegment(Id id,
			SegmentationUnit segmentationUnit) throws IOException;

	// --------------------------------------------------------------------

	Set<Id> getSimilarTracks(Id id) throws IOException;

	boolean isAwayEqReturn(Id id);

	// --------------------------------------------------------------------

	Track addTrack(Track track) throws IOException;

	Track removeTrack(Id id) throws IOException;

	// --------------------------------------------------------------------
	// --------------------------------------------------------------------
	// --------------------------------------------------------------------

	void addListener(DatasourceFascadeListener l);

	void removeListener(DatasourceFascadeListener l);

	/**
	 * Returns a collection of common waypoints.
	 * 
	 * @return collection of common waypoints.
	 */
	Set<WayPoint> getCommonWaypoints();

}
