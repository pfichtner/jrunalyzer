package com.github.pfichtner.jrunalyser.ui.tracklist;

import java.io.IOException;

import javax.annotation.Nullable;

import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;

public class TrackRow {

	private final DatasourceFascade dsf;
	private final Track track;
	private final MinMaxElevation mme;
	private final Integer simCount;

	public TrackRow(DatasourceFascade dsf, Id id) throws IOException {
		this.dsf = dsf;
		this.track = dsf.loadTrack(id);
		WayPoint min = this.track.getStatistics().getMinElevation();
		WayPoint max = this.track.getStatistics().getMaxElevation();
		this.mme = min == null || max == null ? null : new MinMaxElevation(min,
				max);
		this.simCount = Integer.valueOf(dsf.getSimilarTracks(id).size());
	}

	public Track getTrack() {
		return this.track;
	}

	@Nullable
	public MinMaxElevation getMinMaxElevation() {
		return this.mme;
	}

	public Optional<Statistics> getBestSegment(SegmentationUnit segmentationUnit) {
		try {
			return this.dsf.loadBestSegment(this.track.getId(),
					segmentationUnit);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	public Integer getSimCount() {
		return this.simCount;
	}

	public Boolean isAwayEqReturn() {
		return Boolean.valueOf(this.dsf.isAwayEqReturn(this.track.getId()));
	}

}